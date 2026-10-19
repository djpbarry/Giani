# REVISION_LOG

Narrative record of decisions, changes, and lessons for GIANI. Keep in sync with
`DEVELOPMENT_PLAN.md` — after any repo change, update both in the same pass.

## 2026-10-10 — Initial plan

- Authored `DEVELOPMENT_PLAN.md` (mirroring the sibling `IAClassLibrary` and
  `ADAPT` plans) and `AGENTS.md`.
- Documented current state: Java 11 / `pom-scijava:40.0.0` / v3.4.9, no tests, no
  Maven wrapper, stale untagged `IAClassLibrary:fe92f24c6e` pin, BSD-vs-GPL
  license mismatch, hard-coded developer paths, no `plugins.config`.

## 2026-10-10 — Dependency modernisation & versioning convention

- Bumped parent `pom-scijava` `40.0.0` → `45.1.0` and added the explicit
  `central` repository (pom-scijava ≥ 45 drops the implicit Maven Central).
- Set `scijava.jvm.version=21` (required: `IAClassLibrary` ≥ 2.0.x is Java 21).
- Re-pinned `IAClassLibrary` from untagged commit `fe92f24c6e` to tagged release
  `v2.0.23` (still `net.calm.iaclasslibrary`; no package rename yet).
- Updated CI (`maven.yml`) from JDK 11 → JDK 21.
- Adopted the sibling versioning convention (plain `X.Y.Z`, no `-SNAPSHOT`; bump
  on every change; Conventional Commits; tag `vX.Y.Z`). Version bumped
  `3.4.9` → `3.4.10` for this `chore`.
- Not built locally (no Maven on PATH and no wrapper yet); verify via CI.

## 2026-10-10 — Timelapse (4D) & tracking planned

- Added `DEVELOPMENT_PLAN.md` **Phase H** to extend GIANI from per-series 3D
  stacks to 4D (3D + time): treat each timepoint as an independent 3D stack
  (H1), persist per-timepoint segmentations/measurements (H2), and track the
  segmented objects across time (H3).
- **Decision (recorded as Proposed decision 8):** use the TrackMate API *inside*
  GIANI (option b) rather than exporting to TrackMate (option a). TrackMate
  tracks a single flat object type, whereas GIANI objects are hierarchical
  (cell → nucleus → punctae); exporting would flatten and lose the parent–child
  links, while tracking in-process keeps the hierarchy and only delegates the
  lineage to TrackMate. TrackMate is already a GIANI dependency.
- Tracking design: one TrackMate `Spot` per cell per timepoint (represented by
  its nucleus centroid + radius, since cells are seeded 1:1 by nuclei), run a LAP
  tracker, and let children (nucleus measurements, punctae) inherit the parent
  cell's track ID via the existing `Cell ID` association.
- Open items: target object (cells vs nuclei vs both), motion model + linking
  penalties, and whether `BioFormatsImg` already exposes `getSizeT`/time indexing
  or needs an `IAClassLibrary` change.

## 2026-10-10 — inTRACKtive result viewing planned

- Added `DEVELOPMENT_PLAN.md` **H4** to export the Phase H cell lineage for
  `inTRACKtive` (royerlab/inTRACKtive), a **browser-based** cell-tracking viewer
  (not a Fiji plugin) that consumes the **Ultrack CSV format**:
  `track_id, t, z, y, x, parent_track_id` (+ optional `radius`/attribute
  columns), converted to Zarr via `intracktive convert`/`open`.
- GIANI's role is limited to emitting `GIANI_tracks.csv` (track ID = TrackMate
  track ID, `t`/`z`/`y`/`x` = cell centroid in calibrated units, `parent_track_id`
  from division events); the user runs `intracktive open` themselves — no
  inTRACKtive/Python dependency inside GIANI.
- Recorded a model-mapping gotcha: Ultrack uses a tree/forest model
  (`parent_track_id` encodes division), whereas TrackMate uses a graph (division =
  spot with two successors), so H4 needs a translation step that re-labels each
  post-division branch as its own `track_id`. Division detection requires
  `Settings.setAllowTrackSplitting(true)`.
- Noted that inTRACKtive shows one track type (the cell lineage); nuclei/punctae
  stay in `GIANI_Output.csv`/`Spot_Data.csv` and `ResultsBrowser`, not inTRACKtive.

## 2026-10-10 — Citation metadata added

- Added `CITATION.cff` (Citation File Format 1.2.0) at the repo root, and
  documented it as **B3** in `DEVELOPMENT_PLAN.md`.
- Author: David J Barry, ORCID `0000-0003-2763-5244` (The Francis Crick
  Institute). `preferred-citation` is the J Cell Sci 2022 paper
  (doi `10.1242/jcs.259511`).
- Declared `license: GPL-3.0-or-later`, consistent with the headers and B1;
  the POM's BSD-2 declaration remains the outstanding inconsistency to fix.
- `version`/`date-released` are placeholders (`3.4.10`) and must be kept in step
  with `pom.xml` at each `vX.Y.Z` tag.
- Not validated locally (no `pyyaml`; installing packages was declined); the CFF
  was hand-checked against the CFF 1.2.0 schema.

## 2026-10-10 — Positioning vs MIA: finish the plan, then feature-freeze

- Reviewed **MIA** (`mianalysis/mia`): a GPL-3, actively-maintained Fiji/ImageJ
  modular workflow framework (200+ modules, native 5D, built-in tracking +
  TrackMate integration). It overlaps and exceeds GIANI's core capability, and
  already covers most of our planned Phase H (4D/tracking) out of the box.
- Context recovered from the maintainer: GIANI was originally an alternative to
  **Imaris' cell detection and tracking** (weak for 3D). Its niche is the user
  who just wants to detect cells.
- **Decision (Proposed decision 9):** complete Phases A–H, then treat GIANI as
  **feature-complete** — no significant new features after that. MIA is the
  stronger general tool; GIANI competes on **interface**, not capability.
- Added **Phase I — User-interface friendliness** and milestone **M7** (the final
  planned milestone): friendlier wizard, sensible non-zero defaults (removes the
  hard-coded developer paths from D1), fewer required decisions, inline help, and
  a quick-start flow in `README.md`. The overall priority is to make the simple
  cell-detection path accessible to non-experts.
- Added a "Scope & positioning" section to `DEVELOPMENT_PLAN.md`; plan to point
  users at **MIA** for anything beyond GIANI's scope.
- Explicitly considered and rejected **archiving** GIANI: the opinionated,
  hierarchical, guided workflow retains a distinct use case, and archiving would
  strand existing users and the published citation.

## 2026-10-10 — `.gitignore` expanded (Phase A3)

- Added `.idea/`, `*.iml`, `.DS_Store`, and `Thumbs.db` to `.gitignore`, closing
  out Phase A3 (the `.idea/` directory was previously untracked in the working
  tree).
- Bumped version `3.4.10` → `3.4.11` for this `chore`, and kept `CITATION.cff`
  `version` in step.

## 2026-10-10 — Maven wrapper added (Phase A1)

- Added `mvnw`, `mvnw.cmd`, and `.mvn/wrapper/maven-wrapper.properties`
  (script-only wrapper, pinned to Maven `3.9.16`, `wrapperVersion=3.3.4`),
  copied verbatim from the sibling `ADAPT` `development` branch so the three
  repos share an identical wrapper setup.
- `mvnw` must be committed with the executable bit set (CI runs `./mvnw`); use
  `git add --chmod=+x mvnw` (the repo has `core.filemode=false`, so a plain
  `chmod +x` will not survive `git add`).
- Bumped version `3.4.11` → `3.4.12` for this `chore`, and kept `CITATION.cff`
  `version` in step.

## 2026-10-10 — CI hardened (Phase A2)

- Updated `.github/workflows/maven.yml` to run `./mvnw` (the wrapper) instead of
  a system `mvn`, cache Maven deps via `setup-java`'s `cache: 'maven'`, and move
  to `actions/checkout@v5` + `actions/setup-java@v5` with `distribution:
  'temurin'` (matching the `ADAPT` workflow).
- Kept the `PAT` + `--settings mvn_settings.xml` wiring for now; it is removed
  separately in A4 once a clean CI run confirms it is vestigial.
- Bumped version `3.4.12` → `3.4.13` for this `chore`, and kept `CITATION.cff`
  `version` in step.

## 2026-10-10 — GitHub Packages wiring removed from CI (Phase A4)

- Removed `--settings mvn_settings.xml`, `-Dinternal.repo.password="$PAT"`, and
  the `PAT` env from `.github/workflows/maven.yml`; the build is now just
  `./mvnw --batch-mode --update-snapshots verify`.
- Confirmed no `pom.xml` dependency resolves from `maven.pkg.github.com` (deps
  are JitPack + SciJava + explicit Central), so the GitHub Packages profile is
  vestigial for building — same finding as ADAPT.
- `mvn_settings.xml` is retained (unreferenced) pending a clean CI run, then it
  can be deleted. The `distributionManagement` section in `pom.xml` is kept for
  any future GitHub Packages publishing.
- Updated `AGENTS.md` build instructions to reflect the wrapper + no-settings
  build.
- Bumped version `3.4.13` → `3.4.14` for this `chore`, and kept `CITATION.cff`
  `version` in step.

## 2026-10-10 — License reconciled (Phase B1)

- Confirmed **GPL-3.0-or-later** with the maintainer. `license.copyrightOwners`
  stays **"Francis Crick Institute"** (maintainer was there when development
  started; this deliberately differs from ADAPT's "David Barry").
- `pom.xml`: `<licenses>` → "GNU General Public License v3.0 or later" + URL,
  `license.licenseName` `bsd_2` → `gpl_v3`. `LICENSE` and `CITATION.cff` already
  agreed.
- Header tidy-up (B1.2): replaced the three NetBeans "change this header" stubs
  (`FilteringPanel`, `TopHatFilterPanel`, `SegmentationPanel`) and added the
  missing GPL header to `GIANIUI`, `GIANIParamInfos`, and `GIANIMacroExecutor`.
- Bumped version `3.4.14` → `3.4.15` for this `chore`, and kept `CITATION.cff`
  `version` in step.

## 2026-10-10 — JUnit 5 harness + tests (Phase C, M2)

- Added `junit-jupiter-api` + `junit-jupiter-engine` (test scope, parent-managed)
  to `pom.xml`; `pom-scijava` already wires surefire (JUnit Platform) + JaCoCo.
- Added `GianiDefaultParamsTest` (4), `DeprecatedPropsTest` (3), and
  `PipelineBuilderTest` (3) — 10 tests, all green via `./mvnw test`.
- `PipelineBuilderTest` pins the fixed, index-coupled stage ordering and asserts
  the maxima-finder `propLabels` reference real `GianiDefaultParams` constants.
- Confirmed the local build works: `JAVA_HOME="<jdk21>" cmd /c mvnw.cmd test` on
  Windows. The Windows `JAVA_HOME` here defaults to JDK 11, which is too old for
  the Java 21 target, so JDK 21 must be set explicitly.
- Deferred (item 3): executor-output CSV/properties golden tests — need a
  headless fixture image under `src/test/resources`.
- Bumped version `3.4.15` → `3.4.16` for this `test` change, and kept
  `CITATION.cff` `version` in step.

## 2026-10-16 — Code hygiene: hard-coded paths & dead code removed (Phase D1)

- Replaced the `GianiDefaultParams` Dropbox/ilastik defaults with
  `System.getProperty("user.home")` (matches the sibling neutral-default pattern).
- Removed the machine-specific LUT fallback (`C:\Users\barryd\Fiji.app\...`) in
  `SegmentationPanel.previewButtonActionPerformed`; now guards on a null
  `IJ.getDirectory("luts")`.
- Stripped dead code: commented-out `main()` and `buildPipeline()` blocks in
  `GIANIUI`, the commented `buildnumber-maven-plugin` in `pom.xml`, commented-out
  parameter constants/setProperty lines in `GianiDefaultParams`, commented-out
  `map.put` lines in `DeprecatedProps`, and the NetBeans `// TODO` stub in
  `SelectInputPanel`.
- `./mvnw test` still green (10/10). Bumped version `3.4.16` → `3.4.17` for this
  `refactor`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — De-duplicated per-channel propLabels wiring (Phase D2)

- Extracted `PipelineBuilder.getFociPropLabels(int)` (builds the per-channel
  label array) and `PipelineBuilder.setDefaultFociParams(Properties, int)` (sets
  the per-channel defaults).
- `PipelineBuilder.getDefaultFociMaximaFinder` and
  `GIANIUI.addAdditionalBlobDetectionPanels` now both call these two methods,
  replacing ~48 lines of duplicated label-building/default-setting with two calls.
  Behaviour is preserved exactly (including the existing "THRESH_DETECT has no
  default" quirk).
- `./mvnw test` still green (10/10). Bumped version `3.4.17` → `3.4.18` for this
  `refactor`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Eliminated mutable static state (Phase D3)

- `GIANIUI.props`: `private static Properties` → `private final Properties props =
  new GianiDefaultParams()` (inline initialisation); removed the `static { ... }`
  block and the now-unused `protected static getProps()` method.
- `SelectInputPanel.inputDirectory`: `private static File` → `private File`.
- `./mvnw test` still green (10/10). Bumped version `3.4.18` → `3.4.19` for this
  `refactor`, and kept `CITATION.cff` `version` in step.
- **Follow-up flagged:** `GianiResultsBrowser` also has a `private static File
  inputDirectory` (same shared-state pattern) — not in D3's scope, left for a
  later pass.

## 2026-10-16 — Normalised CLI logging (Phase D4)

- Replaced the four `System.out.println` calls in `GIANI.main` (the `-h` help
  text and the three validation/exit messages) with `IJ.log`, so all GIANI output
  now goes through `IJ.log`. The existing `GenUtils.logError` catch blocks were
  already actionable and were left unchanged.
- `./mvnw test` still green (10/10). Bumped version `3.4.19` → `3.4.20` for this
  `refactor`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Fixed `-n` argument fall-through (bug)

- Added the missing `break` after `case "-n"` in `GIANI.main`'s argument switch.
  Previously `-n <job>` fell through into `case "-s"` and also overwrote `series`
  with the job number.
- `./mvnw test` still green (10/10). Bumped version `3.4.20` → `3.4.21` for this
  `fix`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Resolved plugin discovery (Phase D5)

- Added `src/main/resources/plugins.config` registering the macro extension:
  `Extensions, "GIANI", net.calm.giani.macro.GIANIMacroExecutor`.
- Determined and documented (in `AGENTS.md`): the Swing GUI is launched via
  `GIANI.main()` (not a `PlugIn`, so no Fiji menu entry); the macro extension is
  now registered; and `GianiResultsBrowser` is orphaned (no `PlugIn`/`main`,
  never instantiated in the codebase) — open question for a later pass.
- Bumped version `3.4.21` → `3.4.22` for this `chore`, and kept `CITATION.cff`
  `version` in step.

## 2026-10-16 — mcib3d-plugins upgrade investigated, deferred (Phase F3)

- Investigated upgrading `mcib3d-plugins` from the stale GitHub-mirror coordinate
  (`com.github.mcib3d:mcib3d-plugins:3.96.3`) to the modern Framagit coordinate
  (`org.framagit.mcib3d:mcib3d-plugins`).
- Findings: GIANI currently has a version split — `mcib3d-core:4.0.93` (via
  `IAClassLibrary`) + `mcib3d-plugins:3.96.3`. The GitHub mirror is stale (last
  tag `3.96.3`); the Framagit repo has `4.0.93`/`4.1.0`/`4.1.7`.
- `4.0.93` (the version matching the core) **fails to build on JitPack** ("Error";
  its tag's pom declares groupId `com.github.mcib3d` and version `4.0.92`). `4.1.7`
  builds ("ok") but depends on `mcib3d-core:4.1.7`, which would split against the
  `4.0.93` core. `RoiManager3D_2` still exists in 4.x (verified).
- Decision: keep `3.96.3` for now; revisit if the core is bumped in lockstep or
  the results browser (the only `RoiManager3D_2` consumer) is retired. `pom.xml`
  reverted — no version bump, working tree clean.

## 2026-10-16 — Generics: diamond-parameterised raw collections (Phase G1)

- Replaced raw `new ArrayList()`, `new LinkedList()`, and `new LinkedHashMap()`
  instantiations with diamond (`<>`) across `GIANIUI`, `PipelineExecutor`,
  `SelectInputPanel`, `FilteringPanel`, `MaximaFinderPanel`, `GianiResultsBrowser`,
  and `GianiResultsTable` (14 sites). Behaviour-preserving.
- `./mvnw test` still green (10/10). Bumped version `3.4.22` → `3.4.23` for this
  `refactor`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Try-with-resources for BioFormatsImg (Phase G2)

- `PipelineExecutor.run()`: wrapped the per-file `BioFormatsImg` in
  try-with-resources, so the Bio-Formats reader is closed after each file (was
  previously leaked).
- `GianiResultsBrowser.openImage()`: replaced the `BioFormatsImg` field with a
  local try-with-resources (reader closed after loading; the `CompositeImage`
  holds the in-memory pixels, so display is unaffected); removed the now-unused
  `img` field.
- `GianiResultsTable`: no change needed — `DataReader.readCSVFile` closes its own
  reader (IAClassLibrary's G4 already made it try-with-resources).
- Flagged, not fixed (out of scope): `PipelineExecutor`'s `ExecutorService` is
  never shut down; `GIANI.getJobDetails` leaks a `BufferedReader`.
- `./mvnw test` still green (10/10). Bumped version `3.4.23` → `3.4.24` for this
  `refactor`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Fixed executor & BufferedReader leaks (Phase G2 follow-up)

- `PipelineExecutor.run()`: added `exec.shutdown()` after the file loop, so the
  single-thread executor's worker is no longer leaked.
- `GIANI.getJobDetails()`: wrapped the `BufferedReader` in try-with-resources, so
  the job-list reader is closed on all exit paths.
- `./mvnw test` still green (10/10). Bumped version `3.4.24` → `3.4.25` for this
  `fix`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Fix job-list EOF detection (robustness)

- `GIANI.getJobDetails()`: replaced the unreliable `while (fileListReader.ready())`
  loop with the idiomatic `while ((line = fileListReader.readLine()) != null)`,
  so the job list is read to EOF regardless of `ready()`'s buffering heuristics.
- `./mvnw test` still green (10/10). Bumped version `3.4.25` → `3.4.26` for this
  `fix`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Modern language features (Phase G3)

- Converted the three `switch` statements to arrow `switch`: `GIANI`'s argument
  parser, `GIANIMacroExecutor.handleExtension`, and `MaximaFinderPanel`'s method
  selector.
- Replaced `instanceof` + cast with pattern matching in `GianiResultsBrowser`
  (`addListenerToRoiManager3DObjectList`, `enableRoiManagerLiveMode`) and
  `GIANIUI.updateLayer`.
- No anonymous-inner-class→lambda candidates in hand-written code (all in
  generated `initComponents()`, left untouched per the plan).
- `./mvnw test` still green (10/10). Bumped version `3.4.26` → `3.4.27` for this
  `refactor`, and kept `CITATION.cff` `version` in step.

## 2026-10-16 — Planned docs consolidation (Phase E1)

- Investigated `djpbarry/giani-docs`: a separate Sphinx + ReadTheDocs repo
  (fork of `readthedocs/tutorial-template`) with `.rst` content + PNG screenshots,
  `sphinx_rtd_theme`, Python 3.10, and a stale `conf.py` `version = '3.4.9'`.
- Decided to pull the docs into this repo (matching ADAPT's in-repo `docs/`
  pattern). Added **Phase E1** to `DEVELOPMENT_PLAN.md` with a 6-step migration
  (re-root into `docs/`, fix `conf.py`, content review, repoint RTD, archive
  `giani-docs`, update links). Open decision: keep `.rst` vs migrate to MyST
  (recommend keep-`.rst` for the move, migrate later).

## 2026-10-16 — Docs consolidation complete (Phase E1, steps 1-2 & 4)

- Re-rooted the Sphinx project into `docs/` (`conf.py`, `index.rst`,
  `requirements.txt`, `Makefile`, `make.bat`, content subdirs + `images/`),
  added a root `.readthedocs.yaml` pointing at `docs/conf.py`, fixed `conf.py`
  (`version` → `3.4.27`, `html_logo` → `overview/images/Icon.png`), and
  corrected `docs/index.rst` toctree/figure paths. Dropped the unused
  `pyproject.toml` (flit boilerplate unrelated to the docs build).
- Repointed ReadTheDocs to `djpbarry/Giani`; the `development` build succeeds at
  https://giani-docs.readthedocs.io/en/development/. RTD slug is still
  `giani-docs`, so `README.md`/`CITATION.cff` links remain valid.
- Archived `giani-docs`. Remaining: content review (defer until code settles).

## 2026-10-16 — Corrected Phase F premise (IAClassLibrary re-pin)

- Verified `djpbarry/IAClassLibrary`: `master` HEAD is the `v2.0.23` tag and the
  library has **not** been package-renamed (still `net.calm.iaclasslibrary`; pom
  declares `net.calm:iaclasslibrary:2.0.23`). The plan's earlier claim of a
  rename to `io.github.djpbarry.iaclasslibrary` was wrong.
- Corrected `DEVELOPMENT_PLAN.md` (overarching aim, stale-pin gotcha, Phase E/F
  notes, package-namespace decision) and `AGENTS.md` (stale Project overview
  version numbers) to reflect that GIANI already pins `v2.0.23`, so F1 is done
  and no import migration is required.

## 2026-10-16 — Phase F2 verified (TrackMate 8.0.0)

- Confirmed `sc.fiji:TrackMate:8.0.0` resolves (managed by `pom-scijava:45.1.0`,
  pulled transitively via `IAClassLibrary:v2.0.23`; `dependency:tree`).
- GIANI's only TrackMate use is `fiji.plugin.trackmate.Spot` in
  `GianiResultsBrowser` (`new Spot(x, y, z, radius, quality)` + `putFeature`),
  which compiles against 8.0.0 (`mvnw test` green). Noted it is dead code (the
  only `loadSpotData()` call is commented out).
- Marked F2 done in `DEVELOPMENT_PLAN.md` (pom-scijava/Java 21 already landed in
  M4).

## 2026-10-16 — Retired results browser, dropped mcib3d-plugins (Phase F3)

- Deleted the unwired `GianiResultsBrowser` and `GianiResultsTable` (dead code,
  never instantiated) and removed the `com.github.mcib3d:mcib3d-plugins:3.96.3`
  dependency + its exclusions from `pom.xml`.
- Resolves the F3 version split: `mcib3d-plugins` existed only for
  `RoiManager3D_2` in that browser. `mcib3d-core:4.0.93` (transitive via
  `IAClassLibrary`) remains as the pipeline's `Objects3DPopulation` result type.
- Also removed GIANI's only TrackMate import (dead `Spot` code). Updated
  `README.md`, `AGENTS.md`, `CITATION.cff`, and bumped version `3.4.27` →
  `3.4.28`.

## 2026-10-17 — Phase C3 executor integration test + Fiji live-test scripts

- Added `PipelineExecutorIntegrationTest` (Phase C item 3), which runs the full
  pipeline headless over `test_data/18.06.10_Experiment.lif_7.ome.tiff` and
  asserts the `*_Output.csv` + `properties.xml` are produced with data.
- Two gotchas found making it pass: (1) the zeroed detector defaults make blob
  detection search at radius 0 / threshold 0 and exhaust the heap, so the test
  seeds the advanced (Hessian) detector with the live-run parameters; (2) the
  CSV is written in the platform default encoding (not UTF-8), so the test reads
  it as ISO-8859-1.
- Gated with `@Tag("integration")` + a `-Pintegration` profile; default
  `mvn test` still runs only the 10 fast unit tests. The integration test needs a
  4 GB JVM (`-Xmx4g` surefire argLine).
- Added `bin/` scripts (`install-to-fiji.cmd`, `run-fiji.cmd`,
  `smoke-test-fiji.cmd`, `local-env.cmd` + `.example`) adapted from ADAPT, and
  live-tested in Fiji: all three entry classes load and a full
  segmentation/measurement run produced a 362 KB CSV.

## 2026-10-17 — Phase H planned (timelapse 4D + lineage)

- Planned Phase H in detail. Key finding: `BioFormatsImg.getSizeT(series)` exists
  but `loadPixelData` does not select a timepoint (no `TBegin/TEnd`), so 4D data
  would load all timepoints into the Z-stack. H1 therefore needs a small
  `IAClassLibrary` change (settable current time + `setTBegin/setTEnd`) before
  the GIANI time loop.
- Documented H1 (time-aware loading + nested loop + `TRACK_TIMELAPSE` /
  `TIME_SELECT`), H2 (per-timepoint `cells`, `Time` column, `-T{time}` zip
  names), and five open decisions (time-loading design, `TRACK_TIMELAPSE`
  semantics, lineage target, output shape, watershed `cells` reset).
- Cross-repo consequence: H1 needs an `IAClassLibrary` release and a GIANI
  re-pin.

## 2026-10-18 — Re-pinned to IAClassLibrary v2.0.30; reconciled time support

- IAClassLibrary released its Phase H ("BioFormatsImg facade & time hardening"):
  the loader now has the 8-arg `loadPixelData` (explicit C/Z/T window),
  `getSizeT`, and time metadata. GIANI re-pinned `v2.0.23` → `v2.0.30` (build
  green, 10/10).
- Gap found: IAClassLibrary Phase H covers only the loader. The process classes
  (`MultiThreadedMaximaFinder`, `MultiThreadedGaussianFilter`,
  `MultiThreadedTopHatFilter`) still use the 4-arg `loadPixelData` (full Z+T), so
  they cannot select a timepoint. Documented in
  `IAClassLibrary-time-support-proposal.md` and in GIANI's Phase H1 as the
  remaining IAClassLibrary work (a process-level `setTime` + 8-arg load).
- Updated GIANI `DEVELOPMENT_PLAN.md` H1 + decisions to reflect the split: loader
  done, process time selection remaining.

## 2026-10-18 — Post-review cleanup

- Removed the orphaned NetBeans `.form` files (`GianiResultsBrowser.form`,
  `GianiResultsTable.form`) left behind when the results browser was retired in
  F3; the empty `ResultsBrowser` package is gone.
- Ignored JVM crash logs (`hs_err_pid*.log`, `replay_pid*.log`) in `.gitignore`
  and deleted the stray `hs_err_pid24916.log` produced by the integration run.
- Ignored `nb-configuration.xml` (local NetBeans project config, machine-specific,
  previously untracked and repeatedly showing as noise).
- Plan corrections: H3's open item "does `BioFormatsImg` expose `getSizeT`" is
  marked resolved (v2.0.30 does); F3 notes the orphaned-form cleanup; the initial
  state section flags the `ResultsBrowser` package as retired.
- Version bumped `3.4.29` → `3.4.30` (`chore`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py` (the latter had drifted to `3.4.27`).
- `./mvnw test` green (10/10).
- Left untracked-but-tracked as-is: `test_data/18.06.10_Experiment.lif_7.ome.tiff`
  is a 33 MB LFS candidate but is currently a normal git blob; converting it is a
  separate decision (rewriting history), not done here.

## 2026-10-18 — Responded to IAClassLibrary process-parameter note (R2)

- Reviewed `GIANI-process-params-note.md` from the IAClassLibrary team (R2a typed
  `ProcessParams` facade; R2b canonical `Parameter` with stable key vs label).
- Confirmed the premise against GIANI: label-as-key / index coupling runs through
  `PipelineBuilder`, `PipelineExecutor.setup`, the wizard panels (display text,
  props key and tooltip key all share `propLabels[i]`), `GIANIParamInfos` and the
  macro API; `DeprecatedProps` already exists because label renames changed keys.
- Recorded Proposed decision 10 in `DEVELOPMENT_PLAN.md`: accept R2a; R2b
  conditional (backward-compatible loading of label-keyed property files,
  per-channel indexed parameters, per-instance namespacing, deprecated
  `Properties` overload, macro API keeps labels) and scheduled after Phase H;
  fallback is to keep label keys indefinitely. Phase H's `setTime` request stays
  independent of R2b.
- Appended GIANI's reply to the end of `GIANI-process-params-note.md`.
- Docs-only; no code or version change.

## 2026-10-18 — Planned Phase I wizard simplification

- Reviewed `GIANIUI`: the wizard assembles the fixed pipeline inline in
  `initComponents()` and drives it with `addProcess()`/`removeProcess()` during
  navigation, duplicating `buildFullPipeline` and creating the index-coupling
  fragility. The pipeline is fixed, so the incremental build is unnecessary.
- Recorded Phase I1 in `DEVELOPMENT_PLAN.md`: single source of truth
  (`buildFullPipeline`), wizard as a parameter collector, decoupled on-demand
  preview. TrackMate is a partial template (Settings/execution separation, typed
  params via R2) but not its plugin extensibility. The guided wizard UX stays.
- Decided H1 lands headless-first (executor time loop + params via properties
  file, no GUI panel); the timelapse UI panel is deferred to I1.
- Updated the M7 milestone to include the wizard simplification. Docs-only; no
  code change.
