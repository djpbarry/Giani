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

**Modernise a ~7-year-old Fiji/ImageJ plugin whose heavy lifting lives in a
library that has since been modernised under a new package namespace.** The
immediate driver is that GIANI pins `IAClassLibrary` at an untagged commit
(`fe92f24c6e`) that predates that library's modernisation (Java 21, GPL-3, and a
package rename from `net.calm.iaclasslibrary` to `io.github.djpbarry.iaclasslibrary`).
Aligning with the modernised library is a breaking change and the single largest
item of work; everything else (tests, hygiene, CI, docs) makes that migration
safe to do.

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

## Current state (context for the plan)

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
  `src/main/java/net/calm/giani/{exec,ui,gianiparams,ResultsBrowser,macro}`.

### Key architectural facts

- GIANI is mostly **glue**: the actual image-processing primitives
  (`MultiThreadedMaximaFinder`, `MultiThreadedGaussianFilter`,
  `MultiThreadedTopHatFilter`, `MultiThreadedWatershed`,
  `MultiThreadedROIConstructor`, `MultiThreadedColocalise`, `ProcessPipeline`,
  `BioFormatsImg`, `PropertyWriter`) all come from `IAClassLibrary`. The two
  mcib3d types GIANI uses directly are `mcib3d.geom.Objects3DPopulation` and
  (in the results browser) `mcib_plugins.tools.RoiManager3D_2`.
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

- **License mismatch:** `pom.xml` declares Simplified BSD
  (`<license><name>Simplified BSD License</name>` at `pom.xml:82`,
  `license.licenseName=bsd_2` at `pom.xml:138`), but `LICENSE` is GPL-3.0 and
  every source header is GPL-3.0. Also `license.copyrightOwners` is set to
  "Francis Crick Institute"; the sibling repos concluded copyright is
  **David Barry** (development predates the Crick) — confirm for GIANI.
- **Stale upstream pin:** `IAClassLibrary` is pinned at commit `fe92f24c6e`
  (`pom.xml:158`), the same untagged commit ADAPT previously pinned. The library
  has since been modernised: Java 21, GPL-3, and the package root renamed
  `net.calm.iaclasslibrary` → `io.github.djpbarry.iaclasslibrary`. Repinning is
  therefore a **breaking change** (every `net.calm.iaclasslibrary.*` import
  breaks) and the core of Phase F.
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
  `pom.xml` at each `vX.Y.Z` tag (currently `3.4.10`; `date-released` is a
  placeholder until the first real tagged release); (ii) confirm the authors of
  `preferred-citation` against the journal's author list.

---

## Phase C — Introduce tests (the biggest maintainability win)

**Status (2026-10-10): items 1–2 done** — JUnit 5 wired (`junit-jupiter-api` +
`-engine`, parent-managed); `GianiDefaultParamsTest`, `DeprecatedPropsTest`, and
`PipelineBuilderTest` added (10 tests green). Item 3 (executor-output golden
tests) is deferred pending a headless fixture image.

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
3. **CSV/property golden tests** for the output written by
   `PipelineExecutor` (the `GIANI_Output.csv` + saved `Properties`) if a headless
   fixture image can be checked in under `src/test/resources`.

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
also has a `static File inputDirectory` — the same pattern, flagged for a
follow-up.

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
`GianiResultsBrowser` is **orphaned** (no `PlugIn`/`main`, never instantiated) —
flagged as an open question for whether it should get a Fiji menu entry.

Determine and document how GIANI is discovered in Fiji:
- Is it installed by dropping the jar into `Fiji.app/plugins/` and run via the
  main class, or via the macro extension?
- The `GIANIMacroExecutor` (`MacroExtension`) requires registration (typically a
  `plugins.config` `Macros`/`Extensions` line, or a `_`-prefixed jar).
- If a `plugins.config` is the intended mechanism, add it (listing the UI, the
  results browser, and the macro extension); otherwise document the current
  install procedure in `README.md`/`AGENTS.md`.

---

## Phase E — Documentation

- **`README.md`** already has a solid overview and link to ReadTheDocs
  (`https://giani-docs.readthedocs.io/`). Add: build instructions (`./mvnw verify`),
  the headless CLI usage (the `-f/-l/-p/-n/-s/-o` flags), and the Fiji install
  procedure (once D5 resolves it).
- **`AGENTS.md`** (already present) documents architecture, conventions, and
  gotchas. Keep it in sync as Phases D/F land (especially the package rename and
  any pipeline reordering).
- **Javadoc:** the `exec` package is reasonably documented; the `ui` panels and
  `ResultsBrowser` have little class/method Javadoc. Add it to the public API as
  part of the refactor.

---

## Phase F — Upstream coordination & re-pin (the breaking change, do with tests in place)

GIANI's single largest risk is its stale `IAClassLibrary` pin. The library has
been modernised (Java 21, GPL-3, package rename to
`io.github.djpbarry.iaclasslibrary`, tagged releases). Re-pinning is breaking:

### F1. Re-pin `IAClassLibrary` to a tagged release

1. Confirm the target tag (e.g. `v2.0.x`).
2. Migrate every `net.calm.iaclasslibrary.*` import to
   `io.github.djpbarry.iaclasslibrary.*` (this is the bulk of the work; it
   touches `exec`, `ui`, `gianiparams`, `macro`, and `ResultsBrowser`).
3. Reconcile any API changes the library made during its own modernisation
   (deprecated/removed classes GIANI references, e.g. any `IAClasses` usage).
4. Gate on `mvn verify` + the Phase C pipeline-wiring tests.

### F2. Coordinate the Java / TrackMate / pom-scijava bump

- Bump the parent to `pom-scijava:45.1.0` and target **Java 21**
  (`scijava.jvm.version=21`), in lockstep with the re-pinned library.
- Confirm the TrackMate version (parent-managed `8.0.0` after the bump) is
  compatible with GIANI's `ResultsBrowser` usage of `fiji.plugin.trackmate.Spot`.

### F3. Decide on `mcib3d-plugins`

**Status (2026-10-16): deferred — keep `3.96.3` for now.** Investigated an
upgrade; the modern coordinate is `org.framagit.mcib3d:mcib3d-plugins`, but
`4.0.93` (the version matching the core GIANI already gets via IAClassLibrary)
is **broken on JitPack** (build "Error" — its tag's pom is internally
inconsistent: groupId `com.github.mcib3d`, version `4.0.92`). The newer `4.1.7`
builds cleanly but depends on `mcib3d-core:4.1.7`, which would split against the
`4.0.93` core from IAClassLibrary. `RoiManager3D_2` still exists in 4.x (no
class-removal breakage), so an upgrade remains feasible once the core is bumped
in lockstep (or the results browser is retired, at which point `mcib3d-plugins`
can be dropped entirely). Reverted to `com.github.mcib3d:mcib3d-plugins:3.96.3`.

GIANI uses `mcib3d.geom.Objects3DPopulation` (passed throughout the pipeline) and
`mcib_plugins.tools.RoiManager3D_2` (results browser). Confirm these remain
available at the pinned `3.96.3` (or the current release) under Java 21.

---

## Phase G — Modern Java modernisation (Java 21)

Once Phase F lands, apply the same mechanical modernisation the siblings used,
ordered by risk (behaviour-preserving first):

1. **Collections/generics:** ✔ Done (2026-10-16). Replaced the raw
   `new ArrayList()`/`new LinkedList()`/`new LinkedHashMap()` instantiations with
   diamond (`<>`) across `GIANIUI`, `PipelineExecutor`, `SelectInputPanel`,
   `FilteringPanel`, `MaximaFinderPanel`, `GianiResultsBrowser`, and
   `GianiResultsTable` (14 sites).
2. **Try-with-resources:** ✔ Done (2026-10-16). Wrapped the per-file
   `BioFormatsImg` in `PipelineExecutor.run()` in try-with-resources (reader is
   now closed per file), and converted `GianiResultsBrowser.openImage`'s
   `BioFormatsImg` field to a local try-with-resources (reader closed after
   loading; the in-memory `CompositeImage` is unaffected). `GianiResultsTable`
   needs no change — `DataReader.readCSVFile` already closes its reader in
   IAClassLibrary. Then fixed the two flagged leaks: `PipelineExecutor`'s
   `ExecutorService` is now `shutdown()`, and `GIANI.getJobDetails`'s
   `BufferedReader` is now try-with-resources.
3. **Language features:** ✔ Done (2026-10-16). Converted the three `switch`
   statements to arrow `switch` (`GIANI`'s arg parser, `GIANIMacroExecutor`'s
   `handleExtension`, `MaximaFinderPanel`'s method selector) and replaced
   `instanceof` + cast with pattern matching in `GianiResultsBrowser`
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

`PipelineExecutor.run()` currently loops over series only
(`img.getSeriesCount()`, `img.getSizeZ(s)`); Bio-Formats exposes the time axis as
`img.getSizeT(s)` (`BioFormatsImg` wraps `loci.formats`). Add a nested time loop
(`for t in 0..getSizeT(s)-1`) so each timepoint re-runs the existing per-stack
pipeline unchanged — each process already receives its frame via
`setup(img, props, propLabels)`, so a stage is re-run per timepoint exactly as it
is re-run per series today. New parameter(s) (`TRACK_TIMELAPSE` and, if needed, a
time/frame selector) follow the existing `GianiDefaultParams` + `propLabels` +
UI-panel wiring convention.

### H2. Persist per-timepoint segmentations and measurements (not just the last one)

The segmentation state is a single shared `Objects3DPopulation cells` created in
`GIANI.run()` and passed through the whole pipeline; the measurement stage reads
that population. Across the series loop this is acceptable because each series
overwrites and is measured in turn, but tracking needs *all* timepoints
simultaneously. Persist each timepoint's segmentation (the existing
`Objects3DPopulation` zip export, currently named `...-S{series}_{label}.zip`)
extended with a time index (`-T{time}`), and emit a time column in
`GIANI_Output.csv` (or per-timepoint CSVs) so downstream consumers can group rows
by frame.

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
- GIANI **already depends on TrackMate** (`fiji.plugin.trackmate.Spot` is used in
  `ResultsBrowser`), so (b) adds no new dependency.

**Design for (b):**

1. Build a TrackMate `SpotCollection` with one spot per cell per timepoint, keyed
   by TrackMate `frame = time`. Represent each cell by its **nucleus centroid +
   equivalent radius** (nuclei are the seeds, so the cell↔nucleus map is 1:1, and
   nucleus centroids are more stable than whole-cell centroids). Attach GIANI's
   per-object measurements (intensity, morphology) as `Spot` features so they
   survive into the tracking and can gate/display tracks.
2. Run one of TrackMate's **LAP trackers** (`SimpleFastLAPTracker`,
   `SparseLAPTracker`) with the calibration from `img.getCalibration(series)` so
   linking distances are in physical units, not pixels.
3. Read back the `TrackModel`: each track ID is the **cell lineage**. Because
   cells↔nuclei are 1:1 and punctae already carry a `Cell ID`, the children
   (nucleus measurements, punctae) **inherit the parent cell's track ID** — no
   independent tracking of children required.
4. Write a lineage table (track ID per cell per timepoint) alongside
   `GIANI_Output.csv` (or as an extra column), and surface tracks in the
   `ResultsBrowser`.

**Note on punctae/spot tracking:** if the goal is to track punctae themselves as
independent objects (rather than cell lineage), that is a separate, second
TrackMate run over the punctae — but it should still happen inside GIANI using
the same (b) pattern, retaining the `Cell ID`/`Track ID` association.

**Open items to confirm before implementation:**

- Target object for lineage: **cells** (recommended) vs nuclei vs both.
- Tracking motion model (Brownian vs linear motion) and gap-closing/linking
  penalties; expose as parameters or use sensible defaults.
- Whether `BioFormatsImg` already exposes `getSizeT`/time indexing, or needs an
  `IAClassLibrary` change (coordinate with Phase F).

### H4. Export the lineage for inTRACKtive

`inTRACKtive` (royerlab/inTRACKtive) is a **browser-based** viewer for cell
tracking data — not a Fiji plugin. It consumes the **Ultrack CSV format**,
`track_id, t, z, y, x, parent_track_id` (plus optional `radius` and arbitrary
attribute columns), converted to a Zarr bundle via `intracktive convert` /
`intracktive open`. This is an easy, high-value target for the H3 lineage and
reinforces tracking **cells** as the primary lineage object:

- Emit a `GIANI_tracks.csv` in Ultrack format: `track_id` = TrackMate track ID
  (integer, from 1), `t` = time index (continuous, 0-based), `z/y/x` = cell
  centroid in calibrated physical units (`img.getCalibration(series)`),
  `parent_track_id` from division events (`-1` when there is no parent).
- Optionally add a `radius` column (cell equivalent radius) and per-cell
  attributes (intensity, punctae count, etc.) so the viewer's `--add_radius` /
  `--add_attribute` can size/colour cells.
- **No inTRACKtive/Python dependency inside GIANI:** GIANI only writes the CSV;
  the user runs `intracktive open GIANI_tracks.csv` (or `convert`/`serve`) to
  view it. A bundled helper script is a later optional nicety.
- **Model-mapping gotcha:** Ultrack uses a forest/tree model (each track is one
  lineage; `parent_track_id` encodes division), whereas TrackMate uses a graph
  (division = a spot with two successors; the daughters stay in the same
  connected component unless re-labelled). H4 therefore needs a small
  translation step — walk the `TrackModel`, detect division events (spots with
  >1 successor, enabled via `Settings.setAllowTrackSplitting(true)`), and
  re-label each post-division branch as its own `track_id` with the parent's
  `track_id` as `parent_track_id`.
- **Hierarchy note:** inTRACKtive visualises one track type (the cell lineage);
  nuclei and punctae are not shown as separate objects, so their parent–child
  relationships stay in `GIANI_Output.csv` / `Spot_Data.csv` and are surfaced in
  the `ResultsBrowser` rather than inTRACKtive.

---

## Proposed decisions

To be confirmed with the maintainer; the defaults follow the sibling repos'
already-resolved decisions.

1. **License — GPL-3.0-or-later.** `LICENSE` and headers are already GPL-3;
   correct `pom.xml` from BSD-2. `license.copyrightOwners` to be confirmed
   (**David Barry** per the sibling precedent).
2. **Dependencies — stay on JitPack, pin to tags.** Do not vendor; do not use
   GitHub Packages for *dependencies*. Re-pin `IAClassLibrary` from the
   `fe92f24c6e` commit hash to a tagged release; keep `mcib3d-plugins` pinned to
   a tagged release.
3. **Target JDK — Java 21.** ✔ Adopted (2026-10-10). Align with `IAClassLibrary`,
   Fiji's runtime, and TrackMate ≥ 8; `pom-scijava` bumped to `45.1.0` and
   `scijava.jvm.version=21` set.
4. **Package namespace.** The library moved `net.calm.*` →
   `io.github.djpbarry.*`; decide whether GIANI follows suit (renaming
   `net.calm.giani` → `io.github.djpbarry.giani`) in the same pass, or keeps
   `net.calm.giani` and only migrates the *library* imports. Recommend migrating
   both together for consistency.
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

---

## Phase I — User-interface friendliness (the post-freeze priority)

With feature development stopping after Phase H, the interface is where GIANI
earns its place next to MIA. Concrete, low-risk items:

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
   feature freeze — friendlier wizard, sensible defaults, inline help, quick-start
   docs. This is where GIANI differentiates itself from MIA, so it is a
   first-class deliverable rather than an afterthought.

Each milestone is independently shippable. **M1 and M2 are the immediate next
steps** and unblock the risky M4 re-pin. **M7 is the last planned milestone:**
after it, GIANI is feature-complete and maintained, with new functionality
directed to MIA.
