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

## Current state (context for the plan)

- GIANI (`net.calm.giani`) is a **Fiji/ImageJ plugin** (Swing GUI + headless CLI
  + ImageJ macro extension) for batch segmentation/measurement of 3D cell-biology
  images.
- **Build:** Maven, parent `org.scijava:pom-scijava:40.0.0`, version `3.4.9`,
  declared license **Simplified BSD** (`license.licenseName=bsd_2`). The actual
  `LICENSE` file and all source headers are **GPL-3.0** — same BSD/GPL
  contradiction the sibling repos resolved (see Phase B).
- **CI:** `.github/workflows/maven.yml` runs a single
  `mvn --batch-mode --update-snapshots -Dinternal.repo.password="$PAT" --settings mvn_settings.xml verify`
  on **JDK 11**. No Maven wrapper, no dependency caching.
- **Tests:** none — `src/test` does not exist. No lint/format tooling.
- **Dependencies:**
  - `com.github.djpbarry:IAClassLibrary:fe92f24c6e` (JitPack, **untagged commit**)
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

Pin a reproducible toolchain by committing `mvnw`/`mvnw.cmd` + `.mvn/wrapper/`
(mirrors the siblings' A1) so builds don't depend on an unknown system Maven.

### A2. Harden CI

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

Mirror ADAPT's finding: no `pom.xml` dependency resolves from
`maven.pkg.github.com`, so the `--settings mvn_settings.xml` + `PAT` wiring is
likely removable from CI. Verify with one clean CI run before deleting; keep the
`distributionManagement` section for any future GitHub Packages publishing.

---

## Phase B — License & metadata (blocks a clean tag/release)

### B1. Resolve the license (proposed Decision 1)

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

---

## Phase C — Introduce tests (the biggest maintainability win)

No tests exist. GIANI is a GUI/CLI plugin whose logic is entangled with ImageJ
and `IAClassLibrary`, so start with the pure-logic, ImageJ-free classes and the
pipeline-wiring invariants that are cheap to assert.

1. **`gianiparams` pure logic** (no ImageJ runtime needed beyond `IJ.micronSymbol`,
   which is a static string):
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

`PipelineBuilder.getDefaultFociMaximaFinder` and
`GIANIUI.addAdditionalBlobDetectionPanels` both build the same per-channel
`propLabels` array, with divergent default values. Extract a single
authoritative method (in `PipelineBuilder`) that both the pipeline assembly and
the wizard call, so defaults and label indices cannot drift.

### D3. Eliminate mutable static state

- `GIANIUI.props` (`static Properties`) → per-instance field passed down to
  panels (the panels already receive `props` in their constructors).
- `SelectInputPanel.inputDirectory` (`static File`) → instance field.
- `GianiDefaultParams.TITLE` is `static final` and fine, but its dependence on the
  JAR filename is surprising; consider an explicit version source (see Decision 7).

### D4. Normalise error handling & logging

Replace `System.out.println` in `GIANI.java` (CLI argument/validation messages)
with `IJ.log` for consistency with the rest of the codebase (or keep stdout but
make it deliberate — GIANI is also a CLI, so decide and document). Add actionable
messages to broad/empty `catch` blocks (e.g. `GIANI.main`'s `NumberFormatException`
/`NullPointerException` handlers already log via `GenUtils.logError`, which is good).

### D5. Resolve the plugin-discovery question (no `plugins.config`)

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

GIANI uses `mcib3d.geom.Objects3DPopulation` (passed throughout the pipeline) and
`mcib_plugins.tools.RoiManager3D_2` (results browser). Confirm these remain
available at the pinned `3.96.3` (or the current release) under Java 21.

---

## Phase G — Modern Java modernisation (Java 21)

Once Phase F lands, apply the same mechanical modernisation the siblings used,
ordered by risk (behaviour-preserving first):

1. **Collections/generics:** replace raw `new ArrayList()`/`new LinkedList()`
   with diamond-parameterised declarations (GIANIUI, PipelineExecutor, panels).
2. **Try-with-resources:** audit `PipelineExecutor`/`GianiResultsBrowser`/
   `GianiResultsTable` file/reader handling (the results browser uses
   `DataReader.readCSVFile`; verify readers/`CSVPrinter`s are closed).
3. **Language features:** `switch` → arrow, `instanceof` pattern matching,
   anonymous inner classes → lambdas (the many NetBeans `ActionListener` blocks
   in `initComponents` are generated — do **not** hand-edit those; only the
   hand-written `//GEN-FIRST` handlers).
4. **Static-state removal (D3) and logging (D4)** land here as behaviour-preserving
   follow-ups.

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
3. **Target JDK — Java 21.** Align with `IAClassLibrary`, Fiji's runtime, and
   TrackMate ≥ 8; bump `pom-scijava` to `45.1.0` and set
   `scijava.jvm.version=21`.
4. **Package namespace.** The library moved `net.calm.*` →
   `io.github.djpbarry.*`; decide whether GIANI follows suit (renaming
   `net.calm.giani` → `io.github.djpbarry.giani`) in the same pass, or keeps
   `net.calm.giani` and only migrates the *library* imports. Recommend migrating
   both together for consistency.
5. **Versioning — plain `X.Y.Z`, tag `vX.Y.Z`.** Follow the sibling convention:
   no `-SNAPSHOT` suffix; bump `<version>` on every code change
   (`fix`→patch, `feat`→minor, breaking→major); use Conventional Commits.
6. **Plugin framework — stay ImageJ 1.x.** Keep the Swing GUI + `PlugIn`-style
   model; establish the missing `plugins.config` (D5). A SciJava `Command`
   rewrite is out of scope.
7. **Version source.** `GianiDefaultParams.TITLE` currently reads the JAR
   filename; replace with a manifest/build-time version read (matching the
   siblings) so the version and title are robust.

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

Each milestone is independently shippable. **M1 and M2 are the immediate next
steps** and unblock the risky M4 re-pin.
