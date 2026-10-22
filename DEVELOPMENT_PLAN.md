# GIANI Development Plan

> **Keep this plan and `REVISION_LOG.md` in sync:** after any repo change, review
> and update both in the same pass — mark phases/milestones as done or deviated,
> and add a dated narrative entry to the revision log. `REVISION_LOG.md` is the
> single source of truth for what has actually been done; it must not drift stale
> behind the code. (This convention mirrors the sibling repos `IAClassLibrary`
> and `ADAPT`, which maintain the same plan/log pairing on their `development`
> branches.)

This plan outlines a multi-phase effort to modernise GIANI, mirroring the work
already carried out on its upstream dependency `IAClassLibrary` and on the
sibling plugin `ADAPT`. It is grounded in a full review of this repository as of
the plan's writing.

## Overarching aim

**Modernise a ~7-year-old Fiji/ImageJ plugin whose heavy lifting lives in the
upstream `IAClassLibrary`.** GIANI originally pinned that library at an untagged
commit (`fe92f24c6e`) predating its modernisation; it is now pinned to the tagged
release `v2.4.0` (Java 21, GPL-3.0-or-later, parent `pom-scijava:45.1.0`). The
library kept its `net.calm.iaclasslibrary` package (no rename), so aligning with
it is **not** a package-breaking change; the remaining work (tests, hygiene, CI,
docs) makes that pin safe and the plugin maintainable.

- Modernisation takes precedence over backward compatibility of the current
  internals. Where a choice is between a cleaner design and preserving a legacy
  pattern, prefer the cleaner design.
- Deprecate legacy parameter names (the `DeprecatedProps` mechanism already
  exists) rather than breaking old saved property files.
- Prefer deletion of dead/experimental code over keeping it "just in case"
  (recoverable from git).
- Record every decision, mistake, and lesson in `REVISION_LOG.md`.

## Scope & positioning (decided 2026-10-10)

GIANI was originally built as an alternative to **Imaris' cell detection and
tracking** (Imaris for Cell Biologists), whose 3D detection/tracking is widely
regarded as weak. Its niche is the user who *just wants to detect cells*: a
guided wizard, sensible defaults, and one CSV out.

- **MIA (`mianalysis/mia`)** is the stronger tool *overall* — a general-purpose
  Fiji/ImageJ modular workflow framework (200+ modules, native 5D, built-in
  tracking + TrackMate integration, GPL-3, actively maintained). Where MIA
  overlaps GIANI, MIA wins on generality and capability.
- **Decision: finish this plan, then treat GIANI as feature-complete.** No
  significant new feature development beyond the phases below (i.e. Phase H
  lands, then development stops).
- **Where GIANI stays preferable:** the simple cell-detection use case, where a
  guided, opinionated wizard with baked-in hierarchical objects (cell → nucleus
  → punctae) beats assembling a generic modular workflow.
- **Therefore the priority is the *interface*, not features:** make the wizard
  friendlier and the defaults sensible enough that a non-expert can go from a
  folder of images to measurements with minimal configuration. This is the main
  thing that justifies GIANI's continued existence next to MIA.
- **Point users at MIA** for anything beyond that scope (custom pipelines,
  broader 5D workflows, extensive tracking). See Proposed decision 9.
- **Retire the CALM suite.** GIANI historically shipped as the
  `net.calm.calm.GIANI_` package inside the CALM plugin (`calm-3.0.6.jar`),
  which also bundled BALM, FRAP, particle-analysis, and tracking tools. Most of
  that suite is now redundant (covered by GIANI itself, MIA, or other modern
  Fiji plugins), so CALM is retired entirely and GIANI ships as a standalone
  plugin under `net.calm.giani`. (Decided 2026-10-20.)

## Initial state (2026-10-10, context for the plan)

- GIANI (`net.calm.giani`) is a **Fiji/ImageJ plugin** (Swing GUI + headless CLI
  + ImageJ macro extension) for batch segmentation/measurement of 3D cell-biology
  images.
- **Build:** Maven, parent `org.scijava:pom-scijava:45.1.0`, version `3.4.10` (Java 21 via `scijava.jvm.version=21`),
  declared license **Simplified BSD** (`license.licenseName=bsd_2`). The actual
  `LICENSE` file and all source headers are **GPL-3.0** — same BSD/GPL
  contradiction the sibling repos resolved (see Phase B).
- **CI:** `.github/workflows/maven.yml` runs a single
  `mvn --batch-mode --update-snapshots -Dinternal.repo.password="$PAT" --settings mvn_settings.xml verify`
  on **JDK 21**. No Maven wrapper, no dependency caching.
- **Tests:** none — `src/test` does not exist. No lint/format tooling.
- **Dependencies:**
  - `com.github.djpbarry:IAClassLibrary:v2.0.23` (JitPack, tagged)
  - `com.github.mcib3d:mcib3d-plugins:3.96.3` (tagged; `imageware` and
    `mcib3d-core` excluded)
- **Plugin packaging:** `pom.xml` sets `main-class=net.calm.giani.exec.GIANI`,
  and the `maven-dependency-plugin` copies runtime deps into `target/` for a
  runnable jar. There is **no `plugins.config`** in the repo — how GIANI is
  discovered in the Fiji `Plugins>` menu, and how its `MacroExtension` is
  registered, is unclear and must be established before distribution work.
- **Structure:** 17 Java files (plus 11 NetBeans `.form` files) under
  `src/main/java/net/calm/giani/{exec,ui,gianiparams,ResultsBrowser,macro}`
  (the `ResultsBrowser` package was retired in F3 — see below).

### Key architectural facts

- GIANI is mostly **glue**: the actual image-processing primitives
  (`MultiThreadedMaximaFinder`, `MultiThreadedGaussianFilter`,
  `MultiThreadedTopHatFilter`, `MultiThreadedWatershed`,
  `MultiThreadedROIConstructor`, `MultiThreadedColocalise`, `ProcessPipeline`,
  `BioFormatsImg`, `PropertyWriter`) all come from `IAClassLibrary`. The one
  mcib3d type GIANI uses directly is `mcib3d.geom.Objects3DPopulation` (the
  segmentation-result container threaded through the pipeline).
- **Three entry points** share one pipeline builder:
  `exec.GIANI` (CLI + headless), `ui.GIANIUI` (Swing wizard), and
  `macro.GIANIMacroExecutor` (ImageJ `MacroExtension`).
- **The pipeline is a fixed, index-coupled dataflow** (see
  `PipelineBuilder.buildFullPipeline`): maxima-finder → nuclear Gaussian filter
  → nuclear top-hat filter → nuclear watershed → cell Gaussian filter → cell
  watershed → measurement, then optional per-channel foci maxima-finders +
  colocalisation. Stage inputs are referenced by **integer index**
  (`pipeline.getProcess(0)…(5)`), which is duplicated across
  `PipelineBuilder` and `GIANIUI.initComponents`.
- **All parameters are strings in a `Properties` object**, keyed by
  human-readable English labels (constants in `GianiDefaultParams`). Each
  `MultiThreadedProcess` subclass exposes integer label indices (e.g.
  `MultiThreadedMaximaFinder.CHANNEL_SELECT`) that are mapped to
  `GianiDefaultParams` constants via a `String[] propLabels` array, built in
  `PipelineBuilder` and consumed by both the process and the matching UI panel.
- **Per-channel parameters** use `%s%d`-suffixed labels (e.g.
  `"Detect Spots in Channel 3"`), and channel selection is an integer bitmask
  string in `CHAN_FOR_MEASURE`, iterated over bits `0..MAX_CHANNELS-1` (max 10).

### Verified gotchas (from the code review)

- **License mismatch (resolved):** `pom.xml` now declares GPL-3.0-or-later
  (`license.licenseName=gpl_v3`) with copyright owner **Francis Crick Institute**,
  matching `LICENSE` and the source headers (Phase B1).
- **Upstream pin (resolved):** `IAClassLibrary` was pinned at commit `fe92f24c6e`;
  it is now pinned to tagged release `v2.4.0` (`com.github.djpbarry:IAClassLibrary:v2.4.0`
  via JitPack). The library modernised to Java 21 and GPL-3.0-or-later but kept
  its `net.calm.iaclasslibrary` package (its own pom declares
  `net.calm:iaclasslibrary:2.3.0`), so re-pinning was **not** package-breaking.
  See Phase F.
- **Hard-coded developer-specific paths:**
  - `GianiDefaultParams.initialise()` sets `INPUT_DIR_LABEL` to
    `E:\Dropbox (The Francis Crick)\Debugging\Giani\images\subset`
    (`GianiDefaultParams.java:146`) and hard-codes ilastik project/install paths
    (`:193`, `:195`).
  - `SegmentationPanel.previewButtonActionPerformed` falls back to
    `C:\Users\barryd\Fiji.app\luts\glasbey_on_dark.lut` when running headless
    (`SegmentationPanel.java:223`).
- **Mutable static state:** `GIANIUI.props` is a `static Properties` with a
  static initialiser (`GIANIUI.java:42,51-55`); `SelectInputPanel.inputDirectory`
  is `static` (`SelectInputPanel.java:46`). These are shared across instances.
- **`GianiDefaultParams.TITLE` is derived from the JAR filename**
  (`getTitleWithVersion`, `GianiDefaultParams.java:237-248`) and requires the
  filename to start with `GIANI`; otherwise it silently falls back to `"GIANI"`.
  This also determines the output-directory name.
- **Duplicated per-channel wiring:** the foci maxima-finder `propLabels` are set
  up twice — in `PipelineBuilder.getDefaultFociMaximaFinder`
  (`PipelineBuilder.java:260-290`) and again in
  `GIANIUI.addAdditionalBlobDetectionPanels` (`GIANIUI.java:555-605`) — with
  **divergent defaults** (e.g. `STARDIST_PROB` is `"0.0"` in the UI wiring vs the
  `"0.75"` default in `GianiDefaultParams`). This is a drift/bug source.
- **Hard-coded pipeline indices** in `buildFullPipeline` (`PipelineBuilder.java:218-255`)
  are coupled to the same indices in `GIANIUI.initComponents`; reordering a stage
  breaks the other silently.
- **Series with fewer than 2 Z-slices are skipped** (`PipelineExecutor.java:120-123`);
  2D images are silently ignored.
- **Headless mode** sets `java.awt.headless=true`, logs via `ij.IJ.log`, prints
  CLI errors via `System.out.println`, and calls `System.exit(0)` on completion
  and several early-exit paths (`GIANI.java`).
- **`DebugTools.setRootLevel("WARN")`** is duplicated in `GIANI.run()` and
  `PipelineExecutor.run()`; omit it in a new entry path and console output is
  extremely noisy.
- **NetBeans generated-code stubs:** every UI `.java` has an `initComponents()`
  (and `//GEN-*`-delimited fields) that is regenerated by the Form Editor and
  must not be hand-edited. `SelectInputPanel.java:315` still contains a raw
  NetBeans `// TODO add your handling code here:` stub.
- **Raw generic types** are widespread (`new ArrayList()` in
  `PipelineExecutor.java:90`, `new LinkedList()` in `GIANIUI.java:43`, etc.).
- **Commented-out dead code** persists: `GIANIUI`'s commented `main()` and
  `buildPipeline()` (`GIANIUI.java:485-515, 661-671`), the commented-out
  `buildnumber-maven-plugin` block in `pom.xml`, and many commented-out parameter
  constants in `GianiDefaultParams`/`DeprecatedProps`.
- **No `plugins.config`** — plugin/macro discovery in Fiji is unverified; likely
  GIANI is launched by dropping the jar + deps into `Fiji.app/plugins/` and
  invoking the main class, but the macro extension needs explicit registration.
- **Large LFS asset:** `assets/Test_Data.zip` is a ~335 MB Git LFS blob
  (`.gitattributes` marks `*.zip`); treat it as LFS, not a normal blob.
- **`.idea/` is untracked but not gitignored** (`.gitignore` only excludes
  `/build/`, `/dist/`, `/target/`).
- **`mvn_settings.xml` is likely vestigial for building** — it adds a
  GitHub Packages repo/credential, but no `pom.xml` dependency resolves from
  `maven.pkg.github.com` (deps are JitPack + SciJava). Note however that
  `distributionManagement` *publishes* to GitHub Packages, so the settings are
  release-relevant, not build-relevant.

---

## Phase A — Build & CI modernisation

### A1. Add a Maven wrapper

✔ Done (2026-10-10). Added `mvnw`/`mvnw.cmd` + `.mvn/wrapper/maven-wrapper.properties`
(script-only type, pinned to Maven 3.9.16), copied from the sibling `ADAPT`
`development` branch so the wrapper matches the shared setup exactly.

Pin a reproducible toolchain by committing `mvnw`/`mvnw.cmd` + `.mvn/wrapper/`
(mirrors the siblings' A1) so builds don't depend on an unknown system Maven.

### A2. Harden CI

✔ Done (2026-10-10), items 1–3. CI now uses `./mvnw` (the wrapper), caches
Maven deps via `setup-java`'s built-in `cache: 'maven'`, and pins JDK 21 with
`distribution: 'temurin'` on `actions/checkout@v5` + `actions/setup-java@v5`
(matching the `ADAPT` workflow). Item 4 (split `build`/`test` jobs) is deferred
until Phase C lands.

`.github/workflows/maven.yml` currently runs a single `mvn verify` on JDK 11 with
`--settings mvn_settings.xml` and a `PAT` secret.

1. Use the wrapper (`./mvnw verify`) instead of a system `mvn`.
2. Cache Maven dependencies (`actions/cache` on `~/.m2/repository`).
3. Move to JDK 21 (see Phase F / proposed Decision 3) and `pom-scijava:45.1.0`.
4. Split future `build` and `test` jobs once Phase C lands.

### A3. Expand `.gitignore`

Add `.idea/`, `*.iml`, and OS files. (`.idea/` is currently an untracked
directory in the working tree.)

### A4. Remove/confirm the `mvn_settings.xml` GitHub Packages profile

✔ Done (2026-10-10), CI wiring removed. The `./mvnw` build no longer passes
`--settings mvn_settings.xml` / `-Dinternal.repo.password` and no longer sets the
`PAT` secret. `mvn_settings.xml` is retained (unreferenced) and can be deleted
once a clean CI run confirms the build — no `pom.xml` dependency resolves from
`maven.pkg.github.com` (deps are JitPack + SciJava). The `distributionManagement`
section in `pom.xml` is kept for any future GitHub Packages publishing.

Mirror ADAPT's finding: no `pom.xml` dependency resolves from
`maven.pkg.github.com`, so the `--settings mvn_settings.xml` + `PAT` wiring is
likely removable from CI. Verify with one clean CI run before deleting; keep the
`distributionManagement` section for any future GitHub Packages publishing.

---

## Phase B — License & metadata (blocks a clean tag/release)

### B1. Resolve the license (proposed Decision 1)

✔ Done (2026-10-10). Confirmed **GPL-3.0-or-later** with the maintainer;
`license.copyrightOwners` stays **"Francis Crick Institute"** (the maintainer was
there when development started, overriding the ADAPT precedent of "David Barry").
`pom.xml` corrected (`<licenses>` → GPL-3.0-or-later + URL, `license.licenseName`
→ `gpl_v3`). Also fixed the three NetBeans "change this header" stubs
(`FilteringPanel`, `TopHatFilterPanel`, `SegmentationPanel`) and added the
missing GPL header to `GIANIUI`, `GIANIParamInfos`, and `GIANIMacroExecutor`.

`LICENSE` and all source headers are GPL-3.0, but `pom.xml` declares Simplified
BSD. Confirm with the maintainer that **GPL-3.0** is intended (consistent with
the headers, the `LICENSE` file, and the sibling repos' resolution), then:

1. Correct `pom.xml` `<licenses>`, `license.licenseName` (→ `gpl_v3`), and
   `license.copyrightOwners` (confirm **David Barry** vs "Francis Crick
   Institute").
2. Source headers are already GPL-3.0; verify there are no NetBeans "change this
   header" stubs left to clean.

### B2. Tag the repo

The repo has no tags beyond implicit history. Once the license and the Phase F
re-pin are done, cut a fresh semver tag (`vX.Y.Z`) so downstream consumers (and
JitPack) can resolve a tagged release rather than a commit hash.

### B3. Add a `CITATION.cff` (citation metadata)

Community-standard Citation File Format file so GitHub's "Cite this repository"
widget and reference managers pick up the correct citation.

- **Added** `CITATION.cff` (CFF 1.2.0): author **David J Barry**
  (ORCID `0000-0003-2763-5244`, The Francis Crick Institute), GPL-3.0-or-later
  (per B1), `repository-code` = the GitHub repo, and a `preferred-citation` to
  the J Cell Sci 2022 paper (doi `10.1242/jcs.259511`).
- **Follow-ups:** (i) version/`date-released` must be kept in step with
  `pom.xml` at each `vX.Y.Z` tag (`date-released` is a placeholder until the
  first real tagged release); (ii) confirm the authors of
  `preferred-citation` against the journal's author list.

---

## Phase C — Introduce tests (the biggest maintainability win)

**Status (2026-10-17): items 1–3 done.** JUnit 5 wired (`junit-jupiter-api` +
`-engine`, parent-managed); `GianiDefaultParamsTest`, `DeprecatedPropsTest`, and
`PipelineBuilderTest` added (10 tests green). Item 3 is done as
`PipelineExecutorIntegrationTest` (tagged `integration`, gated behind
`-Pintegration`).

No tests exist. GIANI is a GUI/CLI plugin whose logic is entangled with ImageJ
and `IAClassLibrary`, so start with the pure-logic, ImageJ-free classes and the
pipeline-wiring invariants that are cheap to assert.

1. **`gianiparams` pure logic** (no ImageJ runtime needed beyond `IJ.micronSymbol`,
   which is a static string): ✔ Done.
   - `GianiDefaultParams` — defaults are all present and correctly typed.
   - `DeprecatedProps.getDepMap()` — old→new name migration covers the known
     renames, and `mapControlChanges()` sets `NUC_CENTROID_LOCALISATION_METHOD`
     consistently with the blob/Hessian booleans.
2. **Pipeline-wiring golden tests** (the highest-value, lowest-cost target):
   assert `PipelineBuilder.buildFullPipeline` produces the expected stage
   sequence and that the `propLabels` mappings reference real
   `GianiDefaultParams` constants. This pins the index-coupled ordering that is
   currently only implicit and duplicated across two files.
3. **Executor-output integration test** ✔ Done (2026-10-17): runs the full
   pipeline headless over `test_data/18.06.10_Experiment.lif_7.ome.tiff` and
   asserts the `*_Output.csv` + `properties.xml` are produced with data. Tagged
   `integration` (excluded from default `mvn test`; run via `-Pintegration`).

---

## Phase D — Code hygiene & refactoring

### D1. Remove hard-coded paths and dead code (do first)

✔ Done (2026-10-10). Replaced the Dropbox/ilastik defaults in
`GianiDefaultParams.initialise()` with `System.getProperty("user.home")`; removed
the machine-specific LUT fallback in `SegmentationPanel` (now guards on a null
`IJ.getDirectory("luts")`); stripped the commented-out `main()`/`buildPipeline()`
blocks in `GIANIUI`, the commented `buildnumber-maven-plugin` in `pom.xml`, the
commented-out parameter constants/setProperty lines in `GianiDefaultParams`, the
commented-out `map.put` lines in `DeprecatedProps`, and the NetBeans `// TODO`
stub in `SelectInputPanel`.

- Replace the `GianiDefaultParams` Dropbox/ilastik defaults
  (`GianiDefaultParams.java:146,193,195`) with neutral defaults (empty or
  `user.home`), matching the pattern the sibling repos adopted.
- Fix `SegmentationPanel.java:223` — the hard-coded `C:\Users\barryd\...` LUT
  fallback should not contain a machine-specific username; resolve the LUT via
  `IJ.getDirectory("luts")` or bundle the LUT as a resource.
- Strip the commented-out `main()` / `buildPipeline()` blocks in `GIANIUI`, the
  commented `buildnumber-maven-plugin` in `pom.xml`, and the commented-out
  parameter constants in `GianiDefaultParams`/`DeprecatedProps`.
- Remove the raw NetBeans `// TODO` stub in `SelectInputPanel.java:315`.

### D2. De-duplicate the per-channel `propLabels` wiring

✔ Done (2026-10-16). Extracted `PipelineBuilder.getFociPropLabels(int)` (builds
the per-channel label array) and `PipelineBuilder.setDefaultFociParams(Properties,
int)` (sets the per-channel defaults). `getDefaultFociMaximaFinder` and
`GIANIUI.addAdditionalBlobDetectionPanels` now both call these, so the label
indices and defaults can no longer drift between the two copies.

`PipelineBuilder.getDefaultFociMaximaFinder` and
`GIANIUI.addAdditionalBlobDetectionPanels` both build the same per-channel
`propLabels` array, with divergent default values. Extract a single
authoritative method (in `PipelineBuilder`) that both the pipeline assembly and
the wizard call, so defaults and label indices cannot drift.

### D3. Eliminate mutable static state

✔ Done (2026-10-16), first two items. `GIANIUI.props` is now a `final` instance
field (initialised inline, static initialiser and dead `getProps()` removed);
`SelectInputPanel.inputDirectory` is now an instance field. `GianiDefaultParams.TITLE`
is left as-is (Decision 7 is a separate, later item). Note: `GianiResultsBrowser`
had the same `static File inputDirectory` pattern, but it was retired in F3.

- `GIANIUI.props` (`static Properties`) → per-instance field passed down to
  panels (the panels already receive `props` in their constructors).
- `SelectInputPanel.inputDirectory` (`static File`) → instance field.
- `GianiDefaultParams.TITLE` is `static final` and fine, but its dependence on the
  JAR filename is surprising; consider an explicit version source (see Decision 7).

### D4. Normalise error handling & logging

✔ Done (2026-10-16). Replaced the four `System.out.println` calls in
`GIANI.main` (the `-h` help text and the three validation/exit messages) with
`IJ.log`, so all GIANI output now goes through the codebase's single logging
path. The existing `GenUtils.logError` catch blocks in `main` were already
actionable and were left as-is.

Replace `System.out.println` in `GIANI.java` (CLI argument/validation messages)
with `IJ.log` for consistency with the rest of the codebase (or keep stdout but
make it deliberate — GIANI is also a CLI, so decide and document). Add actionable
messages to broad/empty `catch` blocks (e.g. `GIANI.main`'s `NumberFormatException`
/`NullPointerException` handlers already log via `GenUtils.logError`, which is good).

### D5. Resolve the plugin-discovery question (no `plugins.config`)

✔ Done (2026-10-16), determination + minimal fix. Added
`src/main/resources/plugins.config` registering the macro extension
(`Extensions, "GIANI", net.calm.giani.macro.GIANIMacroExecutor`). Determined and
documented (in `AGENTS.md`): the Swing GUI is launched via `GIANI.main()` (not a
`PlugIn`, so no menu entry); the macro extension is now registered; and
`GianiResultsBrowser` was **orphaned** (no `PlugIn`/`main`, never instantiated);
it was later retired in F3 rather than wired to a Fiji menu entry.

Determine and document how GIANI is discovered in Fiji:
- Is it installed by dropping the jar into `Fiji.app/plugins/` and run via the
  main class, or via the macro extension?
- The `GIANIMacroExecutor` (`MacroExtension`) requires registration (typically a
  `plugins.config` `Macros`/`Extensions` line, or a `_`-prefixed jar).
- If a `plugins.config` is the intended mechanism, add it (registering the macro
  extension); otherwise document the current install procedure in
  `README.md`/`AGENTS.md`.

---

## Phase E — Documentation

- **`README.md`** already has a solid overview and link to ReadTheDocs
  (`https://giani-docs.readthedocs.io/`). Add: build instructions (`./mvnw verify`),
  the headless CLI usage (the `-f/-l/-p/-n/-s/-o` flags), and the Fiji install
  procedure (once D5 resolves it).
- **`AGENTS.md`** (already present) documents architecture, conventions, and
  gotchas. Keep it in sync as Phases D/F land (especially any dependency-pin or
  pipeline-reordering changes).
- **Javadoc:** the `exec` package is reasonably documented; the `ui` panels have
  little class/method Javadoc. Add it to the public API as part of the refactor.

### E1. Consolidate ReadTheDocs into this repo

**Status: complete (content review deferred).** GIANI's user docs live in a separate repo
(`djpbarry/giani-docs`), a Sphinx + ReadTheDocs project forked from
`readthedocs/tutorial-template`. It contains `.rst` content under
`docs/{overview,start,cells,nuclei,measure,cluster,complete,test}` plus PNG
screenshots, with `conf.py`/`index.rst` at the repo root, `.readthedocs.yaml`
(Python 3.10, `sphinx.configuration: conf.py`, PDF output), and a single
dependency `sphinx_rtd_theme`. Its `conf.py` has `version = '3.4.9'` (stale) and
`html_logo = 'docs/overview/images/Icon.png'`.

**Decision:** pull the docs into this repo (matching ADAPT's in-repo `docs/`
pattern), so they are versioned and reviewed with the code instead of drifting.

Steps:

1. **Re-root into `docs/`.** Create `docs/` and move the Sphinx project so the
   source root is `docs/` (matching ADAPT): `docs/conf.py`, `docs/index.rst`,
   `docs/requirements.txt`, `docs/Makefile`, `docs/make.bat`, and the content
   subdirs + `images/`. Add `.readthedocs.yaml` at the repo root pointing at
   `docs/conf.py`.
2. **Fix `conf.py`.** Update `version` to the current `3.4.x` (and keep it in
   step with `pom.xml`, as `CITATION.cff` is) and correct the `html_logo` path
   for the new source root.
3. **Content review (after the move).** The docs describe the wizard/installation
   and predate D5's `plugins.config` and the other modernisation; update the
   installation page (Fiji update-site screenshots) and any stale parameter/UI
   text once the code settles.
4. **Repoint ReadTheDocs** from `djpbarry/giani-docs` to `djpbarry/Giani`
   (one-time RTD dashboard action). Done 2026-10-16; the `development` build
   succeeds at https://giani-docs.readthedocs.io/en/development/.
5. **Archive `giani-docs`** (leave a README pointer to the new location). Done 2026-10-16.
6. **Update links** in `README.md`/`AGENTS.md` if the RTD URL changes. No change
   needed: the RTD slug is still `giani-docs`, so `README.md` and `CITATION.cff`
   already point at the right host.

Open decision: keep `.rst` (least work) or migrate to Markdown/MyST to match
ADAPT's docs. Recommend keeping `.rst` for the move and doing a MyST migration
separately (deferred).

---

## Phase F — Upstream coordination & re-pin

**Status (2026-10-16): resolved — no package migration required (pin since moved
to `v2.4.0`).** GIANI already pinned the then-latest `IAClassLibrary` release
`v2.0.23`. Investigation of
`djpbarry/IAClassLibrary` shows `master` HEAD is the `v2.0.23` tag and the
library has **not** renamed its package: its source is still
`net.calm.iaclasslibrary`, and its pom declares
`net.calm:iaclasslibrary:2.0.23` (`<package-name>net.calm.iaclasslibrary</package-name>`).
The earlier assumption of a rename to `io.github.djpbarry.iaclasslibrary` was
incorrect. What the library did modernise: Java 21 (`scijava.jvm.version=21`),
GPL-3.0-or-later, parent `pom-scijava:45.1.0`.

### F1. Re-pin `IAClassLibrary` to a tagged release — ✔ Done (2026-10-16)

1. Confirm the target tag — `v2.0.23` was the latest at the time (since moved to `v2.4.0`).
2. Migrate `net.calm.iaclasslibrary.*` imports — not needed; no rename occurred.
3. Reconcile API changes — none at the same coordinate.
4. Gate on `mvn verify` + the Phase C pipeline-wiring tests (10/10 green).

Optional low-value cleanup: switch the dependency from the JitPack alias
`com.github.djpbarry:IAClassLibrary:v2.4.0` to the library's real coordinate
`net.calm:iaclasslibrary:2.2.1`, but only if that artifact is published to a
real Maven repo (it currently is not). If a future `v3.x` performs the package
rename, revisit this phase.

### F2. Coordinate the Java / TrackMate / pom-scijava bump — ✔ Done (2026-10-16)

- Parent `pom-scijava:45.1.0` + **Java 21** (`scijava.jvm.version=21`) already
  landed in M4 (dependency modernisation).
- TrackMate resolves to `8.0.0` (managed by `pom-scijava:45.1.0`, pulled
  transitively via `IAClassLibrary`; confirmed with `dependency:tree`). GIANI's
  only TrackMate use was the dead `fiji.plugin.trackmate.Spot` code in the
  results browser, removed along with it in F3.

### F3. Decide on `mcib3d-plugins` — ✔ Done (2026-10-16): dropped

Retired the unwired results browser (`GianiResultsBrowser` + `GianiResultsTable`,
dead code) and removed the `com.github.mcib3d:mcib3d-plugins:3.96.3` dependency
and its exclusions. `mcib3d-plugins` existed only to provide
`mcib_plugins.tools.RoiManager3D_2` for that browser, so dropping it resolves the
`3.96.3` vs `4.0.93` core version split. The orphaned NetBeans `.form` files left
behind by the deletion were removed separately (2026-10-18).

`mcib3d-core` (transitive via `IAClassLibrary`, `4.0.93`) remains and is
unavoidable: `mcib3d.geom.Objects3DPopulation` is the segmentation-result
container threaded through IAClassLibrary's pipeline API. No further action.

---

## Phase G — Modern Java modernisation (Java 21)

Once Phase F lands, apply the same mechanical modernisation the siblings used,
ordered by risk (behaviour-preserving first):

1. **Collections/generics:** ✔ Done (2026-10-16). Replaced the raw
   `new ArrayList()`/`new LinkedList()`/`new LinkedHashMap()` instantiations with
   diamond (`<>`) across `GIANIUI`, `PipelineExecutor`, `SelectInputPanel`,
   `FilteringPanel`, `MaximaFinderPanel`, `GianiResultsBrowser`, and
   `GianiResultsTable` (14 sites; the last two retired in F3).
2. **Try-with-resources:** ✔ Done (2026-10-16). Wrapped the per-file
   `BioFormatsImg` in `PipelineExecutor.run()` in try-with-resources (reader is
   now closed per file), and converted `GianiResultsBrowser.openImage`'s
   (later retired in F3)
   `BioFormatsImg` field to a local try-with-resources (reader closed after
   loading; the in-memory `CompositeImage` is unaffected). `GianiResultsTable`
   needs no change — `DataReader.readCSVFile` already closes its reader in
   IAClassLibrary. Then fixed the two flagged leaks: `PipelineExecutor`'s
   `ExecutorService` is now `shutdown()`, and `GIANI.getJobDetails`'s
   `BufferedReader` is now try-with-resources.
3. **Language features:** ✔ Done (2026-10-16). Converted the three `switch`
   statements to arrow `switch` (`GIANI`'s arg parser, `GIANIMacroExecutor`'s
   `handleExtension`, `MaximaFinderPanel`'s method selector) and replaced
   `instanceof` + cast with pattern matching in `GianiResultsBrowser` (later
   retired in F3)
   (`addListenerToRoiManager3DObjectList`, `enableRoiManagerLiveMode`) and
   `GIANIUI.updateLayer`. No anonymous-inner-class→lambda candidates existed in
   hand-written code (they are all in generated `initComponents()`, left alone).
4. **Static-state removal (D3) and logging (D4)** land here as behaviour-preserving
   follow-ups.

---

## Phase H — Timelapse (4D) processing & lineage tracking

Extend GIANI from per-series 3D stacks to 4D (3D + time) datasets, and add
across-time lineage tracking of the segmented objects.

### H1. Treat each timepoint as an independent 3D stack

**Status: done (headless; `IAClassLibrary v2.4.0`).** The timelapse GUI panel is
deferred to I1.

- **IAClassLibrary loader — done (v2.0.30):** `BioFormatsImg` has the 8-arg
  `loadPixelData(series, cBegin, cEnd, zBegin, zEnd, tBegin, tEnd, dimOrder)`,
  `getSizeT(series)`, and time metadata.
- **IAClassLibrary process time selection — done (v2.2.1, R3):** `setTime(int)` /
  `getTime()` live on `MultiThreadedProcess`, and `SourceProcess.loadPixels(...)`
  already loads `tBegin = tEnd = time`. `MaximaFinder` / `GaussianFilter` are
  sources using it; `TopHatFilter` is a transform (reads `inputs[0]`, no pixel
  load). Verified against `v2.3.0`.
- **GIANI (headless) — done:** `PipelineExecutor.run()` nests a time loop inside
  the series loop. After the `getSizeZ(s) < 2` skip and setting
  `SERIES_SELECT_LABEL` / `UNITS`, it computes `nTime = img.getSizeT(s)` and
  resolves the range via the pure `timeRange(nTime, timeSelect, trackTimelapse)`
  helper:
  - `TIME_SELECT` valid (`>= 0 && < nTime`) → that single timepoint;
  - else `TRACK_TIMELAPSE == "true"` → `0..nTime`;
  - else → `0..1` (legacy behaviour, `t = 0` only).
  For each `t` it calls `process.setTime(t)` before `setup(...)` / `submit` /
  `get` per process. `series`/`channel` still come from props; `time` comes from
  `setTime(t)` (harmless on transforms, consumed by sources via `loadPixels`).
- **Parameters — done:** `TRACK_TIMELAPSE` ("Track Timelapse", boolean, default
  `"false"`) and `TIME_SELECT` ("Specific Timepoint", int, default `"-1"`) are
  now in `GianiDefaultParams`. They are GIANI-level params read by
  `PipelineExecutor`, not process slots — no `propLabels` wiring.
- **Decision:** `TRACK_TIMELAPSE` is **explicit** (opt-in), not auto-detected from
  `T > 1` — auto-detection would silently change output shape/runtime for
  existing 4D users (mirrors the `SPECIFIC_SERIES` precedent).
- **H2 coupling — resolved:** the `cells`-reset and the `Time`/`Series` columns
  land in `IAClassLibrary v2.3.0`, so the loop's output is correct and
  distinguishable.
- **Testing — done:** `PipelineExecutorTest` unit-tests the pure
  `timeRange(nTime, timeSelect, trackTimelapse)`; `TimelapseApiTest` guards
  `MultiThreadedProcess.setTime(int)` / `BioFormatsImg.getSizeT(int)`; the
  existing `PipelineExecutorIntegrationTest` covers the `T=1` regression; and
  `TimelapseFixtureTest` + `PipelineExecutorTimelapseIntegrationTest` run the
  full pipeline over the real `Fluo-N3DH-CHO.ome.tiff` fixture (`C=1, Z=5,
  `T=10`) with `TRACK_TIMELAPSE=true` (tagged `integration`).
- **Sequencing — done (headless-first):** executor time loop + params via a
  properties file landed with **no GUI panel**. The timelapse UI panel is
  deferred to Phase I (I1).

### H2. Persist per-timepoint segmentations and measurements (not just the last one)

**Status: done (via `IAClassLibrary v2.4.0`).**

The segmentation state is a single shared `Objects3DPopulation cells` created in
`GIANI.run()` and passed through the pipeline; the measurement stage reads it.
Each timepoint is processed independently because the nuclear watershed's
`initialiseCellsWithNuclei` clears `cells` at the start of each run (verified in
`MultiThreadedWatershed`), so there is no cross-timepoint accumulation.

- ✔ `cells` reset per timepoint — handled by `initialiseCellsWithNuclei`
  (clears the population before re-seeding from nuclei).
- ✔ `Time` column in `GIANI_Output.csv` — written by
  `MultiThreadedROIConstructor.processObjectPop` (groups rows by frame).
- ✔ `-T{time}` index on the per-object exports — `saveAllRois`/`saveAllMasks`
  append a time suffix when `getSizeT > 1`.
- ✔ Output cleanup (`IAClassLibrary v2.4.0`):
  - (a) per-object ROI-manager `*.zip` exports dropped
    (`MultiThreadedROIConstructor.saveAllRois`).
  - (b) masks written into a single folder per object type with Z and T indices
    in the filenames (`MultiThreadedROIConstructor.saveAllMasks`, now using
    idempotent `Files.createDirectories`).

### H3. Tracking: use the TrackMate API *inside* GIANI (recommend option (b))

**Recommendation: (b) — track within GIANI via the TrackMate API**, not (a)
export-and-track-in-TrackMate. Rationale grounded in the object model:

- **TrackMate tracks a single, flat object type** (`Spot` with position, radius,
  and numeric features, grouped into tracks per frame). GIANI's objects are
  **hierarchical**: a cell owns exactly one nucleus (the watershed seed) and N
  punctae/spots (`Spot_Data.csv` already links punctae to cells via `Cell ID`).
- Option (a) requires **flattening** that hierarchy into one spot type and
  round-tripping through TrackMate's GUI/XML, which cannot represent the
  parent–child links; the hierarchy has to be reconstructed afterwards from
  foreign keys. Option (b) keeps the hierarchy in GIANI and uses TrackMate only
  for the lineage, so nothing is lost.
- GIANI **already depends on TrackMate** transitively via `IAClassLibrary` (its
  maxima finder uses TrackMate's `LogDetector`), so (b) adds no new dependency.

**Design for (b):**

1. Build a TrackMate `SpotCollection` with one spot per cell per timepoint, keyed
   by TrackMate `frame = time`. Represent each cell by its **nucleus centroid +
   equivalent radius** (nuclei are the seeds, so the cell↔nucleus map is 1:1, and
   nucleus centroids are more stable than whole-cell centroids). Attach GIANI's
   per-object measurements (intensity, morphology) as `Spot` features so they
   survive into the tracking and can gate/display tracks.
2. Run TrackMate's **`SimpleFastLAPTracker`** (Brownian motion, default
   linking/gap-closing penalties) with the calibration from
   `img.getCalibration(series)` so linking distances are in physical units, not
   pixels.
3. Read back the `TrackModel`: each track ID is the **cell lineage**. Because
   cells↔nuclei are 1:1 and punctae already carry a `Cell ID`, the children
   (nucleus measurements, punctae) **inherit the parent cell's track ID** — no
   independent tracking of children required.
4. Emit the lineage as a `Track ID` column in `GIANI_Output.csv` (building on the
   R5 `Time`/`Series` columns); H4 writes the separate Ultrack `GIANI_tracks.csv`.
   There is no results browser, so lineage is surfaced via the exported CSVs.

**Note on punctae/spot tracking:** if the goal is to track punctae themselves as
independent objects (rather than cell lineage), that is a separate, second
TrackMate run over the punctae — but it should still happen inside GIANI using
the same (b) pattern, retaining the `Cell ID`/`Track ID` association.

**Resolved decisions (2026-10-21) and implementation plan:**

- **Lineage target = cells**, tracked via the **nucleus centroid + equivalent
  radius** (more stable than whole-cell centroids). Cells↔nuclei are 1:1 in
  GIANI's model, so there is no meaningful "cells vs nuclei" toggle; the only
  genuinely different object type is punctae (`Track Spots`, deferred).
- **Parameter:** `TRACK_LINEAGE` (boolean, default `"true"`), read by
  `PipelineExecutor` — headless-first like H1; the GUI checkbox lands in I1.
- **Spot collection:** extract each timepoint's per-cell nucleus centroid/radius
  from the `cells` population *inside* the time loop (before the next frame
  resets it). This passes the `cells` `Objects3DPopulation` into
  `PipelineExecutor` (today only `buildFullPipeline` holds it).
- **Tracker:** `SimpleFastLAPTracker` with default linking/gap-closing settings
  and `allowTrackSplitting(true)` (to detect divisions for H4), using
  `img.getCalibration(series)` so distances are physical. Parameters stay
  defaults for the first cut (expose later).
- **Output:** a `Track ID` column in `GIANI_Output.csv` (builds on the R5
  `Time`/`Series` columns); H4 separately exports the lineage in inTRACKtive's
  Ultrack format (`GIANI_tracks.csv`).
- **Dependency:** declare `sc.fiji:TrackMate` directly in `pom.xml` (package
  `fiji.plugin.trackmate.*`), version left to `pom-scijava`'s dependency
  management (currently `8.0.0`), rather than relying on the transitive pull via
  `IAClassLibrary`.
- **Code shape:** a new `CellLineageTracker` (testable in isolation) collects
  spots per frame and runs TrackMate; `PipelineExecutor` orchestrates it.

### H4. Export the lineage for inTRACKtive

**Status: done (2026-10-22).** `UltrackCsvWriter` writes `GIANI_tracks.csv`
(`track_id,t,z,y,x,parent_track_id,radius`) from `CellLineageTracker.CellTrack`,
using the public track label (`2 + TrackMate track ID`, shared with the H5
masks) and `parent_track_id = -1` (lineage re-labelling deferred). Wired into
`PipelineExecutor` after the time loop; unit-tested.

`inTRACKtive` (royerlab/inTRACKtive) is a **browser-based** viewer for cell
tracking data — not a Fiji plugin. It consumes the **Ultrack CSV format**,
`track_id, t, z, y, x, parent_track_id` (plus optional `radius` and arbitrary
attribute columns), converted to a Zarr bundle via `intracktive convert` /
`intracktive open`. This is an easy, high-value target for the H3 lineage and
reinforces tracking **cells** as the primary lineage object:

- Emit a `GIANI_tracks.csv` in Ultrack format: `track_id` = public track label
  (`2 + TrackMate track ID`, matching the H5 track-labelled masks), `t` = time
  index (continuous, 0-based), `z/y/x` = cell centroid in calibrated physical
  units (`img.getCalibration(series)`), `parent_track_id` = `-1` for now
  (division hierarchy deferred — see the model-mapping note below).
- Optionally add a `radius` column (cell equivalent radius) and per-cell
  attributes (intensity, punctae count, etc.) so the viewer's `--add_radius` /
  `--add_attribute` can size/colour cells.
- **No inTRACKtive/Python dependency inside GIANI:** GIANI only writes the CSV;
  the user runs `intracktive open GIANI_tracks.csv` (or `convert`/`serve`) to
  view it. A bundled helper script is a later optional nicety.
- **Model-mapping (deferred):** Ultrack uses a forest/tree model (each track is
  one lineage; `parent_track_id` encodes division), whereas TrackMate uses a
  graph (division = a spot with two successors; the daughters stay in the same
  connected component). For simplicity H4 emits TrackMate's raw track ID with
  `parent_track_id = -1`, so a dividing cell and its daughters share one track
  ID and no division hierarchy is captured. A future enhancement can walk the
  `TrackModel` (spots with >1 successor via `allowTrackSplitting`) and re-label
  each post-division branch as its own `track_id` + `parent_track_id` to produce
  true lineage IDs (shared with H5).
- **Hierarchy note:** inTRACKtive visualises one track type (the cell lineage);
  nuclei and punctae are not shown as separate objects, so their parent–child
  relationships stay in `GIANI_Output.csv` / `Spot_Data.csv` rather than
  inTRACKtive.

### H5. Track-labelled mask output (visualise the lineage)

**Status: done (2026-10-22).** `TrackMaskWriter` post-processes the Cell/Nucleus
mask PNGs into parallel `<type>_track_masks/` folders, relabelling each pixel to
`TRACK_LABEL_OFFSET (2) + trackId` (0 = background, 1 = untracked). Wired into
`PipelineExecutor` after tracking; unit-tested (`buildRemap` + `remapPixels`).

The H2 masks label each object by its per-timepoint index, so the same physical
cell gets a different label in every timepoint. For tracking, the masks should
instead be labelled by the cell's **TrackMate track ID** so a tracked cell keeps
the same label across all timepoints — making the tracking visually inspectable
directly in Fiji (colour-by-label), complementing H4's `GIANI_tracks.csv` (which
gives centroids/tracks, while these masks give the actual lineage-shaped
segmentation).

- **Scope:** only when `TRACK_LINEAGE=true`. With tracking off, masks keep their
  current per-timepoint labels (behaviour-preserving).
- **Ordering problem:** masks are written per-timepoint inside the time loop, but
  TrackMate's LAP tracker only produces track IDs after all timepoints have been
  seen. Labelling must therefore happen in a post-tracking pass.
- **Two options:**
  - **Post-process the written PNGs (preferred):** after `CellLineageTracker`
    yields `(timepoint, Cell_Index) → track ID`, re-read each `*_masks/*.png`,
    remap the label values, and re-write. Self-contained in GIANI, no
    IAClassLibrary change, disk-bound and scalable to large `T`.
  - **Defer writing:** snapshot each timepoint's label stack (or `cells`) during
    the loop, then write all masks with track IDs after tracking. Cleaner output
    but holds `T` stacks in memory.
- **Label scheme:** in `<type>_track_masks/`, `0` = background, `1` = untracked
  cell (a reserved sentinel), and `2 + trackId` = tracked cell. The same public
  label (`2 + trackId`) is used in `GIANI_tracks.csv`, so masks and CSV agree.
  Shifting track IDs by 2 keeps background/untracked unambiguous; the
  per-timepoint object labels remain available in `<type>_masks/` for
  distinguishing individual objects. A dividing cell and its daughters share one
  label.
- **Future enhancement:** true **lineage IDs** — re-label each post-division
  branch as its own ID with a `parent_track_id` so divisions read as distinct
  lineages (the same translation H4 defers; TrackMate's
  `TimeDirectedNeighborIndex` / directed iterators provide the graph primitives).
- **Hierarchy:** cell masks carry the lineage label; nucleus/punctae masks inherit
  the parent cell's track ID via the existing `Cell ID` association (same as H3).
- **Output shape:** write to a parallel folder set (e.g. `<type>_track_masks/`)
  rather than overwriting `<type>_masks/`, so the per-timepoint and track-labelled
  views coexist.

### Phase H decisions to confirm

1. ~~IAClassLibrary time-loading design~~ — resolved: v2.0.30 added the 8-arg
   `loadPixelData` + time metadata, and v2.2.1 (R3 `SourceProcess.loadPixels`)
   added the process-level `setTime` selector.
2. ~~**`TRACK_TIMELAPSE`**~~ — resolved (2026-10-19): explicit opt-in boolean
   (default `false`), not auto-detect `T > 1`.
3. ~~**Lineage target**~~ — resolved (2026-10-21): cells, via nucleus centroid.
4. ~~**Output shape**~~ — resolved (2026-10-21): `Time`/`Series` columns (done in
   `v2.3.0`) plus a `Track ID` column (H3).
5. ~~**Watershed `cells` reset**~~ — resolved: the library's
   `initialiseCellsWithNuclei(...)` empties `cells` per run (see H2).

Cross-repo consequence: v2.0.30 (loader) and v2.2.1 (process-level `setTime`) are
released, and GIANI re-pinned to `v2.4.0` to pick up the per-timepoint
measurement output. H1 (the GIANI time loop) is now done; no further
IAClassLibrary work is required for H1.

---

## Proposed decisions

To be confirmed with the maintainer; the defaults follow the sibling repos'
already-resolved decisions.

1. **License — GPL-3.0-or-later.** `LICENSE` and headers are already GPL-3;
   correct `pom.xml` from BSD-2. `license.copyrightOwners` to be confirmed
   (**David Barry** per the sibling precedent).
2. **Dependencies — stay on JitPack, pin to tags.** Do not vendor; do not use
   GitHub Packages for *dependencies*. Re-pin `IAClassLibrary` from the
   `fe92f24c6e` commit hash to a tagged release (`mcib3d-plugins` later dropped
   in F3).
3. **Target JDK — Java 21.** ✔ Adopted (2026-10-10). Align with `IAClassLibrary`,
   Fiji's runtime, and TrackMate ≥ 8; `pom-scijava` bumped to `45.1.0` and
   `scijava.jvm.version=21` set.
4. **Package namespace.** The library has **not** moved off `net.calm.*` (still
   `net.calm.iaclasslibrary` at `v2.4.0`), so there is no library-import
   migration to mirror. Any future rename of GIANI itself
   (`net.calm.giani` → `io.github.djpbarry.giani`) is an independent decision,
   not coupled to a library rename.
5. **Versioning — plain `X.Y.Z`, tag `vX.Y.Z`.** ✔ Adopted (2026-10-10). Follow the
   sibling convention: no `-SNAPSHOT` suffix; bump `<version>` on every code
   change (`fix`→patch, `feat`→minor, breaking→major); use Conventional Commits.
   (Version bumped `3.4.9` → `3.4.10` for the dependency `chore`.)
6. **Plugin framework — stay ImageJ 1.x.** Keep the Swing GUI + `PlugIn`-style
   model; establish the missing `plugins.config` (D5). A SciJava `Command`
   rewrite is out of scope.
7. **Version source.** `GianiDefaultParams.TITLE` currently reads the JAR
   filename; replace with a manifest/build-time version read (matching the
   siblings) so the version and title are robust.
8. **Timelapse tracking — TrackMate API in-process (option (b)).** For 4D
   datasets, treat each timepoint as an independent 3D stack and track the
   segmented **cells** (via their nucleus seeds) using the TrackMate API inside
   GIANI, rather than exporting to TrackMate. TrackMate supplies only the
   lineage; GIANI's hierarchy (cell → nucleus → punctae) is preserved by
   inheriting the parent cell's track ID in the children.
9. **Positioning — finish the plan, then feature-freeze; compete on interface.**
   ✔ Adopted (2026-10-10). MIA is the stronger general tool; GIANI's niche is
   simple, guided cell detection (its original Imaris-alternative purpose).
   Complete Phases A–H, then stop adding features and invest effort in making the
   wizard friendlier and the defaults sensible, pointing users at MIA for
   anything more ambitious. See "Scope & positioning".
10. **IAClassLibrary process parameters (R2) — accept R2a; R2b conditional,
    after Phase H.** Proposed (2026-10-18), in response to
    `GIANI-process-params-note.md`. IAClassLibrary plans to replace the
    `Properties` + `String[] propLabels` + integer-index contract with a typed
    `ProcessParams` facade (R2a, label strings stay as keys, no GIANI change) and
    later a canonical `Parameter` with a stable `key()` separate from `label()`
    (R2b, coordinated GIANI migration). GIANI's position:
    - **R2a:** welcome; verify each release against `PipelineExecutorIntegrationTest`
      / `PipelineBuilderTest` before re-pinning.
    - **R2b:** willing, but not before Phase H lands (ideally folded into Phase I),
      and only if: (a) label-keyed property files (CLI `-p`, HPC job lists, macro
      `loadPropertiesFile`, GUI "Load Parameters") still load via a label→key alias
      layer, alongside `DeprecatedProps`; (b) per-channel indexed parameters
      (`%s%d` labels such as `"Detect Spots in Channel 3"`) are supported, e.g.
      `Parameter.forChannel(i)`; (c) parameters are namespaced per process instance,
      because the same class runs twice (nuclear vs cell Gaussian filter/watershed)
      while `SERIES_SELECT_LABEL` stays intentionally shared; (d) the
      `Properties`/label `setup()` path survives as a deprecated overload for at
      least one transition release; (e) the macro API (`Ext.GIANI.setProperty`,
      `getPropertyList`) keeps accepting label strings. GIANI-specific vocabulary
      ("Nuclear Channel") and `GIANIParamInfos` tooltips stay owned by GIANI; the
      library defines generic parameters.
    - **Fallback:** if those conditions are too costly, keep R2a label keys
      indefinitely for GIANI (acceptable given the feature freeze, decision 9).
    - The process-level `setTime(int)` request for Phase H1 must not be coupled
      to, or blocked by, R2b.
11. **Retire the CALM suite; GIANI ships standalone.** ✔ Decided (2026-10-20).
    GIANI originally lived inside CALM (`net.calm.calm.GIANI_` in
    `calm-3.0.6.jar`), alongside BALM/FRAP/particle-analysis/tracking tools that
    are now largely redundant (superseded by GIANI, MIA, or other modern Fiji
    plugins). CALM is retired entirely; GIANI continues as the standalone
    `net.calm.giani` plugin (already the case since it was extracted). No CALM
    dependency or coexistence is required — old `calm-*.jar` /
    `iaclasslibrary-1.0.37.jar` installs should be removed when upgrading.

---

## Phase I — User-interface friendliness (the post-freeze priority)

With feature development stopping after Phase H, the interface is where GIANI
earns its place next to MIA.

### I1. Wizard architecture simplification

The wizard is over-built. `GIANIUI.initComponents()` assembles the fixed pipeline
inline (each `PipelineBuilder.getDefault…()` call creates a process) and wires
each process into a panel for preview, while `nextButton`/`previousButton` call
`addProcess()`/`removeProcess()` to grow/shrink a `ProcessPipeline` during
navigation. That duplicates `buildFullPipeline` (headless) and is the source of the
index-coupling fragility; the pipeline is fixed, so the incremental "build" is
theatre, and the only real reason panels hold process instances is per-stage
preview.

Refactor to:

1. **Single source of truth** — `PipelineBuilder.buildFullPipeline(props, cells)`
   (or a thin `GianiSettings` wrapper) is the only place the pipeline is assembled.
2. **Wizard as a parameter collector** — panels write into the shared config; no
   `addProcess`/`removeProcess`, no inline pipeline in `initComponents`.
3. **Decoupled preview** — a panel previews by constructing its stage on-demand
   from the current config and running it, not by holding the shared pipeline's
   process.

This removes the `componentList` ↔ `ProcessPipeline` coupling and the
`checkIfStarDist` re-wiring hack. TrackMate is a partial template: adopt the
Settings/execution separation and (via R2a/R2b) typed parameters; do **not** adopt
plugin-style extensibility, which is unnecessary for a fixed pipeline. The guided
wizard UX stays — GIANI's niche is opinionated cell detection for non-experts.
This is where the timelapse UI panel (H1, deferred) lands.

### I2. Friendliness & polish

- **Sensible, non-zero defaults** so that a first run with no configuration
  produces useful output: replace the hard-coded developer paths in
  `GianiDefaultParams.initialise()` (the Dropbox/ilastik leftovers flagged in
  D1) with safe, informative defaults, and give filter radii / detection
  parameters sensible starting values rather than `"0.0"`.
- **Reduce the number of required decisions.** Many wizard steps exist for
  flexibility few users need; consider collapsing optional/advanced stages
  behind an "advanced" toggle, leaving a default path of: pick folder → pick
  channels → run.
- **Validate input and explain failures in the UI**, rather than relying on
  `GenUtils.logError` output to the ImageJ log (see D4).
- **Inline help and tooltips** for every parameter (the `GIANIParamInfos`
  tooltip mechanism already exists; audit coverage on the wizard panels).
- **A quick-start flow** in `README.md` (pick folder → channels → run → open
  results) alongside the existing overview, and a pointer to MIA for advanced
  needs.
- **Preview fidelity:** the preview panels are the main differentiator over a
  config file; make sure defaults and previews make it obvious when a
  segmentation is wrong.

This phase is deliberately broad and is expected to be driven by user feedback
once the modernisation lands.

---

## Suggested sequencing & milestones

1. **M1 — Foundations (low risk, high value):** `.gitignore`, Maven wrapper, CI
   hardening, remove `mvn_settings.xml` from CI, license-metadata fix. (Phase A, B1)
2. **M2 — Test harness:** JUnit 5 + `gianiparams`/`DeprecatedProps` tests +
   pipeline-wiring golden tests. (Phase C)
3. **M3 — Hygiene:** remove hard-coded paths/dead code, de-duplicate the
   per-channel wiring, remove static state, fix the LUT fallback. (Phase D)
4. **M4 — Upstream re-pin (the breaking change):** migrate library imports,
   bump Java/pom-scijava, reconcile API changes. Gated on M2. (Phase F)
5. **M5 — Modern Java + docs:** generics/try-with-resources/language features,
   Javadoc, README build/install docs, resolve `plugins.config`. (Phase E, G)
6. **M6 — Timelapse & tracking (Phase H):** the 4D frame loop (H1) and
   per-timepoint persistence (H2) are self-contained and can land early; the
   TrackMate lineage tracking (H3) and the inTRACKtive Ultrack-CSV export (H4)
   depend on M2 (tests) and M4 (the re-pin, to fix the TrackMate version under
   `pom-scijava:45.1.0`) and are a separate, later increment.
7. **M7 — Interface friendliness (Phase I):** the final milestone before the
   feature freeze — wizard architecture simplification (I1), friendlier wizard,
   sensible defaults, inline help, quick-start docs. This is where GIANI
   differentiates itself from MIA, so it is a first-class deliverable rather than
   an afterthought.

Each milestone is independently shippable. **M1 and M2 are the immediate next
steps** and unblock the risky M4 re-pin. **M7 is the last planned milestone:**
after it, GIANI is feature-complete and maintained, with new functionality
directed to MIA.
