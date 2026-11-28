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

## 2026-10-19 — Re-pinned to IAClassLibrary v2.2.1; fixed missing MaximaFinder defaults

- Re-pinned `com.github.djpbarry:IAClassLibrary` from `v2.0.30` to `v2.2.1`
  (R2b typed params + R3 source/transform + R4a executor consolidation).
- The re-pin surfaced a real gap via R2b's "bound-but-missing → fail loudly":
  `GianiDefaultParams.initialise()` never set `NUC_DETECT_MODE` (`METHOD` slot)
  or `NUC_MAXIMA_DETECT_THRESHOLD` (`THRESH_DETECT` slot), so the nuclear maxima
  finder threw `IllegalStateException: Missing value for bound parameter
  'thresh_detect'` in `PipelineExecutorIntegrationTest`.
- Fixed by adding the two defaults: `NUC_DETECT_MODE` → the simple blob detector
  (consistent with `NUC_MAXIMA_DETECT_BLOBS = "true"`) and
  `NUC_MAXIMA_DETECT_THRESHOLD` → `"false"`.
- `./mvnw test` green (10/10) and `./mvnw test -Pintegration` green (11/11;
  full pipeline end-to-end on the test image).
- Version bumped `3.4.30` → `3.4.31` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-10-19 — Swept IAClassLibrary version references to `v2.2.1`

- Corrected `DEVELOPMENT_PLAN.md` where the "current pin" still said `v2.0.30`
  (or Phase F's "then-latest" said `v2.0.23`): overarching aim, upstream-pin
  gotcha, Phase F status/F1, the H1 cross-repo note, and Proposed decision 4 now
  reference `v2.2.1`.
- Docs-only; no code or version change.

## 2026-10-19 — Planned H1 (headless timelapse loop) in detail

- Recorded a concrete H1 implementation plan in `DEVELOPMENT_PLAN.md`:
  - Library side is done in `v2.2.1` (`setTime(int)` + `SourceProcess.loadPixels`
    loads `tBegin = tEnd = time`), so H1 needs no further IAClassLibrary work.
  - GIANI adds a time loop in `PipelineExecutor.run()` (`nTime = getSizeT(s)`,
    range `t0..tEnd` from `TIME_SELECT` / `TRACK_TIMELAPSE` / default `t=0`),
    calling `process.setTime(t)` before each process.
  - New `GianiDefaultParams` keys `TRACK_TIMELAPSE` ("Track Timelapse", default
    `"false"`) and `TIME_SELECT` ("Specific Timepoint", default `"-1"`) — GIANI-level
    params, no `propLabels` wiring.
  - Resolved decision 2: `TRACK_TIMELAPSE` is explicit opt-in, not auto-detect.
  - Flagged H2 coupling (`cells` reset + `Time` column) and the need for a 4D
    (Z×T) fixture to test the loop end-to-end.
- Docs-only; no code change.

## 2026-10-19 — H1 implemented (headless timelapse time loop)

- Added `TRACK_TIMELAPSE` ("Track Timelapse", default `"false"`) and
  `TIME_SELECT` ("Specific Timepoint", default `"-1"`) to `GianiDefaultParams`
  (GIANI-level params, no `propLabels` wiring).
- `PipelineExecutor.run()` now nests a time loop inside the series loop: it
  computes `nTime = img.getSizeT(s)`, resolves `[t0, t1)` via the new pure
  `PipelineExecutor.timeRange(nTime, timeSelect, trackTimelapse)` helper, and
  calls `process.setTime(t)` before each process's `setup(...)` / `submit` /
  `get`.
- `timeRange` semantics: a valid specific timepoint wins; otherwise timelapse
  spans `0..nTime`; otherwise a single `t = 0` frame.
- Re-pinned `IAClassLibrary` `v2.2.1` → `v2.3.0` (process-level `setTime` plus
  per-timepoint measurement output).
- Tests added: `PipelineExecutorTest` (7 unit tests for `timeRange`),
  `TimelapseApiTest` (guards `MultiThreadedProcess.setTime(int)` and
  `BioFormatsImg.getSizeT(int)`), `TimelapseFixtureTest` (fixture needs `Z>=2`
  and `T>=2`), and `PipelineExecutorTimelapseIntegrationTest`
  (`integration`-tagged; full pipeline over a `T=2` fixture with
  `TRACK_TIMELAPSE=true`, asserting a `Time` column and at least 2 distinct
  timepoints).
- H2 remains open: per-timepoint `cells` reset and the `-T{time}` zip naming are
  not yet explicit in GIANI code (the `Time` column is produced by the library).
- No version bump in this commit range (still `3.4.31`), flagged against the
  "bump on every change" convention.

## 2026-10-20 — Decided to retire the CALM suite

- Inspected the live Fiji: GIANI historically shipped inside CALM
  (`calm-3.0.6.jar`, package `net.calm.calm.GIANI_`), which also bundles BALM,
  FRAP, particle-analysis, tracking, and MicroManager tools via its
  `plugins.config` (`Plugins>CALM>GIANI` menu entries + `GIANI Macro Extensions`
  / `GIANI Results Browser`).
- Decision (Proposed decision 11): retire CALM entirely — most of its tools are
  redundant with GIANI, MIA, or other modern Fiji plugins. GIANI continues as a
  standalone plugin (`net.calm.giani`); no CALM dependency or coexistence is
  required.
- Upgrade note: old `calm-*.jar` and `iaclasslibrary-1.0.37.jar` installs
  should be removed when upgrading, to avoid `net.calm.iaclasslibrary`
  classpath clashes with the new `IAClassLibrary` pin.
- Docs-only; no code or version change.

## 2026-10-21 — Output review: drop ROI zips, consolidate masks (planned)

- Reviewed the multi-timepoint output: per-object ROI-manager `*.zip` exports and
  per-timepoint `*_T{n}_masks/` folders are emitted alongside the CSV.
- Recorded two output-format changes in `DEVELOPMENT_PLAN.md` (H2):
  (a) the ROI-manager zips are now superfluous — their only consumer (the
  results browser) was retired in F3 — and should be dropped;
  (b) masks should be written into a single folder per object type with Z/T
  indices in the filenames, rather than one folder per timepoint, for easier
  importing.
- Docs-only; no code or version change.

## 2026-10-21 — Re-pinned to IAClassLibrary v2.4.0 (H2 output cleanup)

- `IAClassLibrary` released the H2 output-cleanup changes requested in
  `IAClassLibrary-output-cleanup-note.md`: (a) dropped the per-object ROI-manager
  `*.zip` exports; (b) consolidated masks into one folder per object type with
  Z/T indices in the filenames; (c) switched `saveAllMasks` to idempotent
  `Files.createDirectories`.
- GIANI re-pinned `v2.3.0` → `v2.4.0` (`pom.xml`). Build green; `./mvnw test`
  passes (20 run, 0 fail, 1 skipped fixture).
- Marked Phase H2 done in `DEVELOPMENT_PLAN.md`.
- Bumped GIANI version `3.4.32` → `3.4.33` (chore), kept in sync across
  `pom.xml`, `CITATION.cff`, and `docs/conf.py`.

## 2026-10-21 — Swapped timelapse fixture to real CTC data; async previews

- Replaced the synthetic `18.06.10_Experiment.lif_7_time.ome.tiff` (a duplicated
  Z-stack, `T=2`) with the real Cell Tracking Challenge `Fluo-N3DH-CHO.ome.tiff`
  (`C=1, Z=5, T=10`, 5.7 MB) as the Phase H1 fixture. The timelapse integration
  test now runs over 10 genuinely distinct timepoints and completes in ~80 s
  (previously ~11 min over 2 identical frames).
- `PipelineExecutorTimelapseIntegrationTest` channel params adjusted for the
  single-channel nuclei dataset (`CELL_SEG_CHAN_SELECT=0`, `CHAN_FOR_MEASURE=1`);
  the `Time`/`Series` column + `>=2` distinct-timepoints assertions pass.
- Recorded the earlier async preview refactor (5 wizard panels now run previews
  on a background thread and marshal results back via `SwingUtilities.invokeLater`,
  a first step toward R4's drop-`Thread` / I1 decoupled preview).
- Version bumped `3.4.31` → `3.5.0` (`feat`: headless timelapse + measurement
  `Time`/`Series` columns), kept in sync across `pom.xml`, `CITATION.cff`, and
  `docs/conf.py`.

## 2026-10-21 — Planned H3 (TrackMate lineage) and resolved its decisions

- Recorded the H3 implementation plan in `DEVELOPMENT_PLAN.md` and resolved the
  open decisions:
  - Lineage target = **cells**, tracked via the **nucleus centroid + equivalent
    radius**. Cells↔nuclei are 1:1, so a "cells vs nuclei" toggle is meaningless;
    the only genuinely different object is punctae (`Track Spots`, deferred).
  - `TRACK_LINEAGE` (boolean, default `"false"`), headless-first; GUI checkbox in I1.
  - Spots extracted from the `cells` population inside the time loop (before reset);
    `PipelineExecutor` gains access to `cells`.
  - `SimpleFastLAPTracker`, default settings, `allowTrackSplitting(true)` for
    divisions, physical units.
  - Output: `Track ID` column in `GIANI_Output.csv` (H4 exports the inTRACKtive
    Ultrack CSV).
  - `sc.fiji:TrackMate` declared directly, version via `pom-scijava` (8.0.0).
  - New `CellLineageTracker` class; `PipelineExecutor` orchestrates.
- Docs-only; no code or version change.

## 2026-10-22 — Merge of H2 re-pin + H3 lineage; git-workflow note

- Merged two diverged lines on `development` (common ancestor `8904643`): the
  local H2 output-cleanup re-pin (`IAClassLibrary v2.4.0`, `3.4.33`) and the
  remote H3 lineage-tracking work (`CellLineageTracker`, `TRACK_LINEAGE`,
  `TrackMate` dep, fixture swap, `3.5.0`). Conflicts resolved across `pom.xml`
  (kept `v2.4.0` + TrackMate + `3.5.0`), `GIANIUI` (3-arg `PipelineExecutor` +
  async run), `CITATION.cff`/`docs/conf.py` (`3.5.0`), and the plan/log.
- Post-mortem: the local commits were never pushed at the end of the session,
  while `origin/development` advanced independently, so the next `pull`
  conflicted. Added a "Git workflow" note to `AGENTS.md` (push at the end of each
  session; `fetch` + check `@{u}` before working; push before pull).
- Added **Phase H5** to `DEVELOPMENT_PLAN.md` — track-labelled masks (label each
  object by its TrackMate track ID rather than per-timepoint index, so tracked
  cells keep the same label across timepoints). Proposed as a post-tracking pass
  over the written PNGs, `TRACK_LINEAGE=true` only. Decided (2026-10-22) to use
  TrackMate's raw track ID for simplicity and defer true lineage IDs
  (post-division branch re-labelling) as a future enhancement in both H4 and H5.
- Version bumped `3.5.0` → `3.5.1` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-10-22 — H4: Ultrack lineage CSV export

- Added `UltrackCsvWriter` (`exec/`) which writes `GIANI_tracks.csv` in
  inTRACKtive's Ultrack format (`track_id,t,z,y,x,parent_track_id,radius`).
- `CellLineageTracker.CellTrack` extended to carry the calibrated nucleus
  centroid + equivalent radius (via `getCenterAsVectorUnit()` /
  `getVolumeUnit()`), so the CSV holds physical units.
- `PipelineExecutor` now accumulates `CellTrack`s across series and writes
  `GIANI_tracks.csv` after the run when `TRACK_LINEAGE=true`.
- Changed `TRACK_LINEAGE` default `"false"` → `"true"` so tracking output is
  visible by default (harmless for single-timepoint runs: no edges → no CSV).
- `parent_track_id` is `-1` for now (lineage/division re-labelling deferred —
  see H4/H5 in the plan).
- Decided the `track_id` column uses the same public label as the H5 masks:
  `TRACK_LABEL_OFFSET (2) + TrackMate track ID` (0 = background, 1 = untracked),
  so the CSV and masks stay consistent.
- `UltrackCsvWriterTest` added (2 tests: sorted output + empty-list skip).
  `./mvnw test` green (22/22).
- Version bumped `3.5.1` → `3.6.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-10-22 — H5: track-labelled masks

- Added `TrackMaskWriter` (`exec/`) which post-processes the Cell and Nucleus
  mask PNGs into parallel `<type>_track_masks/` folders, relabelling each pixel
  to `TRACK_LABEL_OFFSET (2) + trackId` (0 = background, 1 = untracked), matching
  `GIANI_tracks.csv`.
- `CellLineageTracker.CellTrack` gained the per-timepoint mask `label`
  (`nucleus.getValue()`); `PipelineExecutor` runs the remap pass after tracking
  when `TRACK_LINEAGE=true`.
- `TrackMaskWriterTest` added (2 tests: per-timepoint remap + in-place pixel
  remap). `./mvnw test` green (24/24).
- Version bumped `3.6.0` → `3.7.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-10-22 — Planned a tracking parameters panel (Phase I1)

- Noted in `DEVELOPMENT_PLAN.md` (I1) that the wizard should gain a dedicated
  tracking parameters panel: surface `TRACK_TIMELAPSE` / `TRACK_LINEAGE` /
  `TIME_SELECT` as proper controls (today only `Track Timelapse` has a checkbox),
  and later expose tracking tuning (motion model, linking/gap-closing penalties,
  `Track Spots`).
- Docs-only; no code change.

## 2026-10-22 — Output review: CSV cross-referencing gap (planned)

- Reviewed the multi-timepoint `GIANI-3.7.0_Output/`: `GIANI_tracks.csv` and the
  `*_Cell_track_masks/` / `*_Nucleus_track_masks/` folders are correct.
- Found the `Track ID` column is missing from `GIANI_Output.csv` (track IDs only
  in `GIANI_tracks.csv`), and neither CSV carries a shared cell identifier — so
  they can only be cross-referenced by (time + centroid).
- Recorded as a gap in `DEVELOPMENT_PLAN.md` (H3): add a `Track ID` column to
  `GIANI_Output.csv` (and/or a `Cell_Index`/label column to `GIANI_tracks.csv`)
  so the two CSVs are directly linkable.
- Docs-only; no code change.

## 2026-10-25 — Track ID cross-referencing: cytoplasm rows verified consistent

- Verified that `annotateTrackIds` labels the cytoplasm measurement rows
  correctly, not just the nucleus and cell rows. The cell watershed is built
  with `remap=true` (`PipelineBuilder.getDefaultCellSegmenter`), so
  IAClassLibrary's `MapPixels` remaps every cell label onto the nuclear
  segmenter's label space. `cyto.getValue()` therefore equals
  `nucleus.getValue()`, so the results-table `Cell_Index` is the same for all
  three object rows.
- Clarified the `trackKey` third argument, renamed `cellIndex` → `cellLabel` to
  match the value it carries (`CellTrack.label()` / the `Cell_Index` column),
  distinct from `CellTrack.cellIndex()` (the 0-based population position).
- No behavioural change.

## 2026-10-25 — Tracking parameters panel (Phase I1)

- Added a dedicated `TrackingPanel` to the wizard, surfaced immediately after
  input selection, exposing `TRACK_TIMELAPSE`, `TRACK_LINEAGE`, and
  `TIME_SELECT` as proper controls (two toggles + a specific-timepoint field).
- Moved the `Track Timelapse` toggle off `SelectInputPanel` onto the new panel,
  so all three tracking settings live in one place and bind through the same
  `PropertyExtractor` label/field mechanism.
- No headless behaviour change; the parameters are unchanged.

## 2026-10-31 — Tracking toggle semantics + toggle-persistence findings (Phase I1)

- **UX mismatch (logged, deferred to the GUI revamp).** `Track Timelapse` only
  means "analyse every timepoint" (it widens the `Time` loop via
  `PipelineExecutor.timeRange`); it does *not* produce any tracking/linkage
  output. `Track Lineage` is what runs TrackMate and emits `GIANI_tracks.csv`
  (`UltrackCsvWriter`) and the `*_track_masks` folders (`TrackMaskWriter`).
  Confirmed on a live run: with `Track Timelapse = true` and
  `Track Lineage = false`, per-timepoint masks/`Time`/`Series` columns appear
  but there is no `Track ID` column, no `GIANI_tracks.csv`, and no track masks —
  both writers early-return on the empty track list. The label "Track Timelapse"
  is therefore misleading; a future pass should rename it (through
  `DeprecatedProps`) to something like "Process All Timepoints" and/or make
  `Track Lineage` imply multi-timepoint analysis.
- **Found and fixed (committed in `fc89226`):**
  - `TopHatFilterPanel` initialises its toggle with `setSelected(...)` instead of
    `setEnabled(...)` (line 214), so the toggle's on/off state reflects
    `ENABLE_TOP_HAT_FILTER` correctly.
  - Preview handlers now persist *every* panel's state via the reentrancy-guarded
    `GIANIUI.collectPanelState()` before building a fresh preview pipeline, so a
    toggle changed on one panel is visible when previewing a different panel.
- Both toggle issues were the same class of `JToggleButton` `selected`↔`enabled`
  / per-panel-state persistence bug; fixed ahead of the general GUI revamp.

## 2026-10-31 — Consolidated CLAUDE.md into AGENTS.md (docs)

- Deleted `CLAUDE.md` (two overlapping agent-guidance files). Merged its current,
  accurate content into `AGENTS.md`: version/dependency corrections (`3.7.5`,
  `IAClassLibrary v2.4.0`, direct `sc.fiji:TrackMate`), the `PlugIn`/`plugins.config`
  correction, the `STAGE_*` constants table, the `TRACK_TIMELAPSE`-vs-`TRACK_LINEAGE`
  distinction, the always-`clean` / silently-skipping-integration-test gotchas, and
  the three-file version-sync + plan/log-sync conventions.
- Fixed the three integration-test fixture paths for the relocated fixtures
  (`test_data/single_timepoint/…`, `test_data/multiple_timepoints/…`), which were
  silently skipping since the move.

### Plan/log review — known drifts (recorded so they are not lost with CLAUDE.md)

`DEVELOPMENT_PLAN.md` / `REVISION_LOG.md` still carry the following contradictions
against source/output; left in place rather than rewritten:

- H2 marked done, but `spot_summary_data.csv` still discards 9 of 10 timepoints of
  punctae data (upstream `MultiThreadedColocalise`).
- H3 names `SimpleFastLAPTracker`; the code uses `SparseLAPTrackerFactory`.
- H3's calibration step is unimplemented — already-calibrated µm coordinates feed
  TrackMate's unmodified defaults (15 µm linking/gap-closing/splitting limits).
- `allowTrackSplitting(true)` + `parent_track_id = -1` is structurally wrong: both
  daughters share one track label/ID, so divisions are invisible.
- Punctae do not inherit track IDs (`spot_summary_data.csv` has no
  `Time`/`Series`/`Track ID`).
- I1's problem statement describes deleted `addProcess`/`removeProcess`/
  `checkIfStarDist` code in the present tense.
- B2's "no tags" is false — `v3.4.0`/`v3.4.29` exist; the real gap is ~45 bumps with
  no release since `v3.4.29`.
- `CITATION.cff` pairs a current version with a placeholder `date-released: 2019-01-01`.

### Lessons for future projects

1. Keep a short "Current state" block at the top of the plan, rewritten each pass;
   treat narrative as historical only.
2. "Done" must mean tested — "verified by reading upstream source" is PARTIAL.
3. Integration tests that can skip must fail (or be run) in CI.
4. Cross-check decisions made on different days against each other.
5. Deferred items need a trigger + owner, or they silently expire.
6. If you bump the version every commit, tag on a cadence (JitPack resolves tags).
7. Don't describe code in the present tense in plan docs.

- Version bumped `3.7.4` → `3.7.5` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-01 — Fixed GUI startup crash from eager preview build (Phase I1)

- Launching `Plugins > CALM > GIANI` crashed with
  `NumberFormatException: For input string: "Item 1"` in
  `MultiThreadedWatershed.setup`. Root cause: `GIANIUI.initComponents()` built a
  full preview pipeline per panel just to read `.getPropLabels()`, and each build
  ran `collectPanelState()`, which persisted the unpopulated `SelectInputPanel`
  series combo box's placeholder `"Item 1"` into `SERIES_SELECT`; the watershed
  then parsed it as an int.
- Fix (mirrors the existing `getFociPropLabels` pattern): added
  `PipelineBuilder.getStagePropLabels(int)` plus per-stage `get*PropLabels()`
  helpers (single source of truth), refactored the `getDefault*` builders to
  delegate to them, and changed `initComponents` to call `getStagePropLabels(...)`
  instead of building processes for labels.
- Side benefit: removes the startup wart of ~7 eager full-pipeline builds (and 49
  discarded process objects) per launch.
- `./mvnw clean test` green (27/27); `TimelapseFixtureTest` now runs (not skipped)
  against the relocated fixture.
- Version bumped `3.7.5` → `3.7.6` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-01 — Fixed foci (spot) parameters not loading from property files

- Loading a saved parameter file did not restore the sub-cellular spot
  (foci/`MaximaFinderPanel`) parameters. Root cause: `addAdditionalBlobDetectionPanels`
  calls `PipelineBuilder.setDefaultFociParams(props, i)`, which wrote every
  per-channel default with unconditional `setProperty(...)`, clobbering the values
  just loaded from the file.
- Fix: `setDefaultFociParams` now uses `putIfAbsent(...)` so defaults only fill
  keys that were not already present (loaded or previously configured).
- `./mvnw clean test` green (27/27).
- Version bumped `3.7.6` → `3.7.7` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-01 — Lineage re-labelling: divisions now yield parent/child tracks (H4/H5)

- `parent_track_id` was hard-coded `-1`, and with `allowTrackSplitting(true)` both
  daughters of a division stayed in the mother's TrackMate track, so divisions
  were invisible. Implemented the deferred lineage re-labelling.
- `CellLineageTracker.track()` now walks each TrackMate track with
  `TimeDirectedNeighborIndex`: a spot with more than one successor is a division,
  and each daughter starts a fresh lineage whose parent is recorded. `CellTrack`
  gained a `parentTrackId` field.
- `UltrackCsvWriter` now emits the parent's public label (offset by
  `TRACK_LABEL_OFFSET`) or `-1` for roots. The mask labels and the `Track ID`
  column already shared the same offset, so divisions now read as distinct labels
  everywhere.
- `./mvnw clean test` green (27/27); `UltrackCsvWriterTest` updated to cover the
  parent-offset mapping.
- Version bumped `3.7.7` → `3.8.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-01 — Derive tracking linking distance from the data (H3)

- TrackMate was run with `factory.getDefaultSettings()`, leaving the
  linking/gap-closing/splitting distance at the hard-coded 15 unit default even
  though the spot coordinates are already in physical units.
- `CellLineageTracker` now derives the distance from the median observed nucleus
  radius (3×), so it scales with the objects being tracked instead of being a
  fixed value; it falls back to TrackMate's default only when there are no spots.
- Compile verified only; no Fiji reinstall (per user request).
- Version bumped `3.8.0` → `3.8.1` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-01 — Punctae inherit track IDs (re-pin IAClassLibrary v2.5.0)

- `spot_summary_data.csv` previously had no `Time`/`Series` columns and was
  overwritten per timepoint, so per-cell punctae summaries were time-blind and
  could not be joined to lineage tracks.
- Re-pinned `IAClassLibrary` to `v2.5.0` (upstream `MultiThreadedColocalise` now
  writes `Time`/`Series` and accumulates rows across timepoints).
- Added `PipelineExecutor.annotateSpotSummary`, which post-processes
  `spot_summary_data.csv` after tracking to add a `Track ID` column, mapping each
  row's `(series, time, cellIndex)` to its lineage track id (same mechanism as
  `annotateTrackIds` for the main results table).
- `./mvnw clean test` green (28/28), including a new `annotateSpotSummary` unit
  test.
- Version bumped `3.8.1` → `3.9.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-01 — Tracking panel: rename and surface tuning controls (Phase I1)

- Renamed the misleading `Track Timelapse` parameter to
  `Process All Timepoints` (new `GianiDefaultParams.PROCESS_ALL_TIMEPOINTS`,
  default `false`); `DeprecatedProps` maps the old `"Track Timelapse"` key so
  saved property files migrate transparently.
- Added two tracking-tuning parameters, wired through `CellLineageTracker`:
  - `Linking Distance` (`TRACK_LINKING_DISTANCE`, default `-1` = auto) overrides
    the auto-derived `3 × median nucleus radius` value and applies to the linking,
    gap-closing, and splitting max distances.
  - `Max Frame Gap` (`TRACK_MAX_FRAME_GAP`, default `2`) controls
    `KEY_GAP_CLOSING_MAX_FRAME_GAP` (how many frames a track may be missing).
- `CellLineageTracker` now takes `(linkingDistance, maxFrameGap)` in its
  constructor; `PipelineExecutor` parses the two new properties and passes them
  through.
- `TrackingPanel` re-laid out: `Process All Timepoints` toggle, `Specific
  Timepoint`, `Track Lineage`, then `Linking Distance` / `Max Frame Gap`, with
  explanatory tooltips. The motion-model and `Track Spots` tunings remain
  deferred.
- `./mvnw test` green (28/28).
- Version bumped `3.9.0` → `3.10.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-02 — Sensible non-zero defaults (Phase I2, step 1)

- Replaced the degenerate `"0.0"` filter/detection radii in
  `GianiDefaultParams.initialise()` with biological starting values so a
  first run produces useful output without configuration:
  - `NUC_FILT_RAD_LABEL` (nuclear Gaussian filter) `0.0` → `1.0`
  - `CELL_FILT_RAD_LABEL` (cell Gaussian filter) `0.0` → `1.0`
  - `NUC_TOP_HAT_FILT_RAD_LABEL` (top-hat background radius) `0.0` → `10.0`
  - `BLOB_NUC_RAD_LABEL` (simple-detector nuclear radius) `0.0` → `5.0`
  - `NUC_MAXIMA_DETECT_HESSIAN_START_SCALE` (advanced-detector min radius)
    `0.0` → `5.0`
- Left the `"0.0"` quality thresholds (noise tolerance, Hessian quality)
  unchanged: `0.0` means "detect all" and is a reasonable default.
- `GianiDefaultParamsTest` updated to assert the new defaults.
- `./mvnw test` green (28/28).
- Version **not** bumped (left at `3.10.0`, contrary to the bump-every-change
  convention); the next commit (`3.11.0`) covers it. Noted retrospectively.

## 2026-11-02 — "Advanced Options" toggle collapses the wizard (Phase I2, step 2)

- Added an `Advanced Options` button to the wizard's button bar. Off by default,
  it reduces the wizard to the two essential panels — input selection and
  channels-to-measure — giving a default path of *pick folder → pick channels →
  run*. Toggling it reveals the full panel set (nuclear detection/filtering/
  top-hat/segmentation, cell filtering/segmentation, tracking).
- `GIANIUI` now tracks `showAdvanced` and a `corePanels` subset
  (`selectInputPanel` + `measurementPanel`); `updateLayer()`/`checkLayerIndex()`
  navigate the visible list rather than the full `componentList`. The toggle is a
  plain `JButton` (not a `JToggleButton`) so `PropertyExtractor` does not treat it
  as a pipeline property.
- `./mvnw test` green (28/28).
- Version bumped `3.10.0` → `3.11.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-02 — Validate input and surface errors in the UI (Phase I2, step 3)

- Added `GIANIUI.validateInput()`, called at the start of the Run action: it
  rejects a blank input path, a non-existent path, and a directory containing no
  valid image files, showing a `GenUtils.error` dialog (beep + `IJ.error`) instead
  of continuing to fail silently in the executor.
- `./mvnw test` green (28/28).
- Version bumped `3.11.0` → `3.12.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-02 — Audit and fill tooltip coverage (Phase I2, step 4)

- Audited the wizard panels' tooltip coverage and filled the gaps:
  - Added `GIANIParamInfos` entries for the tracking parameters
    (`PROCESS_ALL_TIMEPOINTS`, `TIME_SELECT`, `TRACK_LINEAGE`,
    `TRACK_LINKING_DISTANCE`, `TRACK_MAX_FRAME_GAP`), `ENABLE_TOP_HAT_FILTER`,
    and `NUC_DETECT_MODE` (the method combo previously resolved to a null
    tooltip because `GIANIParamInfos` only had `NUC_CENTROID_LOCALISATION_METHOD`).
  - Switched `TrackingPanel` to look tooltips up from `GIANIParamInfos` instead
    of hard-coded strings.
  - Added the missing tooltip to the top-hat enable toggle in `TopHatFilterPanel`.
- `LocalisationPanel` has no parameter controls (a single "click Run" label), so
  its empty `setToolTips()` is left as-is.
- `./mvnw test` green (28/28).
- Version bumped `3.12.0` → `3.13.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-02 — Quick-start flow in README (Phase I2, step 5)

- Added a "Quick Start" section to `README.md`: launch → pick folder → pick
  channels → run → open results, with a pointer to `Advanced Options` for
  parameter control and to MIA for workflows beyond GIANI's focused scope.
- Docs-only; no code change.
- Version bumped `3.13.0` → `3.13.1` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, and `docs/conf.py`.

## 2026-11-02 — Read the version from the JAR manifest (Decision 7)

- `GianiDefaultParams.TITLE` was built from the JAR's filename (upper-cased,
  required to start with `GIANI`), so a renamed JAR silently lost or misreported
  the version. It now reads `Implementation-Version` from the manifest, which
  `pom-scijava`'s jar config already writes, via a pure `titleFor(version)`
  helper (`GIANI-<version>`, or `GIANI` when there is no manifest).
- Verified with the packaged JAR: `TITLE=GIANI-3.13.2`, unchanged when the JAR
  is renamed to `my-plugin.jar` (previously this fell back to `GIANI`).
- Scope kept narrow: output folder/CSV names still embed the version. Stable
  output names plus a version/commit entry in `properties.xml` were proposed
  and deferred.
- `./mvnw clean package` green (29/29), with a new `titleFor` unit test.
- Version bumped `3.13.1` → `3.13.2` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-02 — Plan/log housekeeping (docs)

- `DEVELOPMENT_PLAN.md`: updated the statements of the *current* IAClassLibrary
  pin from `v2.4.0` to `v2.5.0` (overarching aim, upstream-pin gotcha, F1
  coordinate note, decision 4). The library's own coordinate at `v2.5.0` is
  `net.calm:iaclasslibrary:2.5.0` (read from the JAR's `pom.properties`).
  Historical references ("done in v2.3.0", "re-pinned to v2.4.0 to …") are left
  as written.
- `REVISION_LOG.md`: the "Sensible non-zero defaults" entry lacked a version
  line; `git` shows that commit (`8b3826b`) did not bump the version (stayed
  `3.10.0`), so this is recorded rather than back-filled with a fictitious bump.
- Version bumped `3.13.2` → `3.13.3` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-02 — Skip unreadable input files instead of aborting the run

- When `BioFormatsImg.setId` failed, `PipelineExecutor.run()` logged the error
  but carried on with the uninitialised reader. Probing with a corrupt `.tif`
  showed `getSeriesCount()` returns `-1` (series loop is a no-op) but
  `getFileList()` throws `IllegalStateException` ("call setId(String) first").
  `updateFileList` only caught `IOException`, so the exception escaped `run()`:
  no results CSV, `properties.xml`, tracks or masks were written (including
  results already accumulated for earlier files in a batch), and the CLI still
  exited `0`.
- Reachable via single-file input (`-f`, `-l` job lists, or a single file in
  the GUI), which bypasses `BioFormatsFileLister.obtainValidFileList`; the
  directory path is largely protected because the lister already rejects such
  files (a header-valid but truncated file could plausibly still slip through —
  untested).
- Fix: on `setId` failure, log "… - skipping.", remove the file from the work
  list and `continue` to the next file.
- New `PipelineExecutorUnreadableInputTest` (non-integration): runs the
  executor on a corrupt single file and asserts the run completes and saves
  `properties.xml`. Confirmed it fails without the fix (the
  `IllegalStateException` above) and passes with it.
- `./mvnw clean test` green (30/30).
- Version bumped `3.13.3` → `3.13.4` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.
- Note: the `aaf94fa` commit omitted the new (untracked) test file; it was
  added in the following commit.

## 2026-11-03 — Documented two deferred robustness issues (docs)

- Added a "Known issues (deferred)" section to `DEVELOPMENT_PLAN.md` (Phase I)
  recording two problems found during the unreadable-input fix and deferred
  until after release:
  - header-valid but truncated files in a directory batch may still abort the
    run (unverified; needs a truncated-file reproduction);
  - the CLI always exits `0`, so failed HPC jobs report success.
- Docs-only; no code change.
- Version bumped `3.13.4` → `3.13.5` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-03 — User docs content review (E1 step 3, text)

- Reviewed every `docs/` page against the code and real 3.9.0/3.13.0 output
  folders; the content dated from ~v2.0. Rewrote/updated:
  - **installation:** CALM update site retained (decision: reuse it for GIANI),
    noting the retired CALM tools; fixed the broken inline site list; added an
    "Upgrading from an older version" section (Java 21, stale
    `calm-*`/`giani-*`/`IAClassLibrary-*` JARs).
  - **about:** timelapse/tracking and spot localisation; MIA pointer; citation
    now the J Cell Sci 2022 paper (was the 2020 bioRxiv preprint).
  - **getting started:** GIANI is on `Plugins` directly; documented the
    default path, *Advanced Options*, and *Load Parameters*.
  - **new `start/tracking.rst`:** Tracking panel parameters, the
    "Process All Timepoints must be on" trap, tracking outputs
    (`Track ID`, `GIANI_tracks.csv`, `*_track_masks`), inTRACKtive viewing.
  - **output (`complete/index`):** real folder naming (`GIANI-X.Y.Z_Output`,
    `_(1)` suffix), ROI zips removed, per-type mask folders with `_T_Z`
    names, `Series`/`Time`/`Cell_Index` columns, `spot_summary_data.csv`.
  - **headless (`cluster/index`):** replaced `java -jar` with the Fiji launcher
    form `fiji-windows-x64.exe --headless --main-class=net.calm.giani.exec.GIANI
    -- -f … -p … -o …`, **verified** against the portable Fiji (3.13.0; exit 0,
    full output). Earlier attempts without the `--` divider failed with exit
    code 3 (launcher rejects unrecognized args; `allow-unrecognized-args =
    false` in `config/jaunch/common.toml`). Removed `-h` (never parsed when
    given alone). Added an ImageJ macro section (`Ext.GIANI.*`).
  - **file formats:** correct 2D-skip message, timelapse support, unreadable
    files skipped, current Bio-Formats docs link.
  - **pipeline pages:** five detection methods (added Grey Level
    Thresholding); single filter radius (not x/y/z); current parameter names
    and defaults; *Enable Top Hat Filtering*; spot panels only appear with
    Advanced Options; wiki cross-links replaced with `:doc:` links.
  - `conf.py` copyright → 2019-2026; test-data page notes its outputs are old.
- Not built locally (no Sphinx); internal `:doc:` links checked by script.
  ReadTheDocs builds on push.
- Outstanding (as `.. TODO(...)` comments in the sources): screenshot
  recapture, and verifying macro-extension activation.
- Findings for later code work, recorded with root causes and next steps in
  `DEVELOPMENT_PLAN.md` "Known issues (deferred)": wizard help buttons
  (`getHelpURI`) still point at the GitHub wiki; `GIANI -h` alone is never
  parsed; spot-detection panels are hidden (defaults used) unless Advanced
  Options is on; macro-extension activation is unverified.
- Version bumped `3.13.5` → `3.13.6` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-03 — Live 3.13.6 run reviewed; GUI label/control review planned (I3)

- Installed 3.13.6 into the portable Fiji (removing `giani-3.13.0.jar` and a
  stale CALM-era `iaclasslibrary-1.0.37.jar` from `plugins/`). A live
  timelapse run (CHO fixture, all timepoints, tracking + spots on channel 0)
  confirmed the manifest-derived title under Fiji (`GIANI-3.13.6_Output`) and
  working tracking (77 observations, 8 lineages, `Track ID` in both CSVs).
- The run and a follow-up code review surfaced bugs, to be fixed next:
  - `updateDeprecatedProps` is only called from the GUI; headless and macro
    property loading never migrate old parameter names.
  - StarDist probability/overlap labels are swapped in `MaximaFinderPanel`
    (`:382`/`:393`), so collecting panel state writes each value under the
    other's key.
  - `TrackMaskWriter` remaps every `*_masks` folder, including spot masks,
    using the cell label → track map, producing meaningless
    `*_Spot_track_masks`.
  - `GianiDefaultParams.initialise()` stores the per-channel prefix constants
    (e.g. `"Radius for Simple Spot Detection in Channel "`) as keys, leaving
    unsuffixed junk entries in `properties.xml`.
- Planned **I3** in `DEVELOPMENT_PLAN.md`: tracking panel redesign (replace the
  `-1` sentinels with a *Timepoints to Analyse* dropdown and an *Automatic
  Linking Distance* toggle; disable *Track Lineage* unless all timepoints are
  analysed), shorter panel titles and small text fixes, and parameter renames
  via `DeprecatedProps`. Recorded the key constraint (on-screen labels are the
  `properties.xml` keys, via `PropertyExtractor`), the deferral of 1-based
  channel/series numbering (needs IAClassLibrary), and two open output-format
  questions (spot rows in the main CSV; inconsistent Cell `Label` values).
- Docs-only; no code change.
- Version bumped `3.13.6` → `3.13.7` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-03 — Migrate deprecated parameter names on every loading path

- Only the GUI (`GIANIUI.loadParameters`) called
  `GianiDefaultParams.updateDeprecatedProps` after loading a parameter file.
  Headless runs (`GIANI.init`) and the macro extension
  (`GIANIMacroExecutor.loadPropertiesFile`) loaded via
  `PropertyWriter.loadProperties` directly and never migrated old names, so
  e.g. a pre-3.10 file's `Track Timelapse=true` was silently ignored on HPC
  (only `t=0` analysed). `AGENTS.md` wrongly claimed all paths migrated.
- Fix: new `GianiDefaultParams.loadProperties(props, title, file)` loads and
  then migrates; all three call sites use it, and the now-unused
  `PropertyWriter` imports were removed. `AGENTS.md` updated to require this
  helper for any future loading path.
- New `GianiDefaultParamsTest.loadPropertiesMigratesDeprecatedNames` writes a
  `properties.xml` containing only `Track Timelapse=true` and asserts
  `Process All Timepoints` becomes `true` after loading. (It covers the helper;
  the bug itself was the call sites bypassing it.)
- `./mvnw clean test` green (31/31).
- Version bumped `3.13.7` → `3.13.8` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-04 — AGENTS.md audit: fixed three defects; full suite verified green

- Audited `AGENTS.md` against the code (the "treat status markers as unverified"
  rule applied to `AGENTS.md` itself). Confirmed accurate: manifest-derived
  `TITLE`, the `PROCESS_ALL_TIMEPOINTS` rename + `DeprecatedProps` mapping, all
  three loading paths routed through `GianiDefaultParams.loadProperties`,
  `plugins.config` registering the GUI at the `Plugins` root, 8 `.form` files
  for 9 UI classes, and the 3.13.8 version sync across all four files.
- **Fixed: self-contradictory output filename.** The "Execution flow" bullet
  said results go to `<output-dir>/GIANI_Output.csv`, contradicting the `TITLE`
  gotcha further down. Since 3.13.2 the title is manifest-derived, so a Fiji run
  writes `GIANI-<version>_Output.csv` and only a test/IDE run (no manifest)
  writes `GIANI_Output.csv`. Corrected to `<TITLE>_Output.csv` with both cases
  spelled out.
- **Fixed: a hard-coded machine-specific Fiji path.** The build section named
  `C:\Users\barryd\fiji-latest-portable-nojava\Fiji` — the same class of
  developer-specific absolute path that Phase D1 removed from the code, this
  time in the docs; it does not exist on the current machine. Replaced with the
  `FIJI_DIR` / `bin\local-env.cmd` mechanism that already exists for exactly
  this purpose.
- **Fixed: the `bin/` scripts were undocumented.** `AGENTS.md` had no mention of
  `install-to-fiji.cmd` / `run-fiji.cmd` / `smoke-test-fiji.cmd`, and instead
  documented a manual jar-copy procedure. Now documents the scripts first.
  **Discrepancy recorded rather than silently resolved:** `install-to-fiji.cmd`
  installs `IAClassLibrary` into `jars/` while the manual procedure (used for
  the live 3.13.6 run on 2026-11-03) put both jars in `plugins/`. Both work; the
  doc now prefers the script for consistency. Worth settling properly later.
- **Fixed: two more stale references found while reviewing the diff.** The
  "Execution flow" bullet still described `timeRange(nTime, timeSelect,
  trackTimelapse)` and `TRACK_TIMELAPSE`, both renamed in 3.10.0 (the method
  parameter is `processAllTimepoints`); and the CLI bullet still sent readers to
  the retired GitHub wiki rather than `docs/cluster/index.rst`. Both corrected,
  and the lone-`-h` known issue is now cross-referenced from the CLI bullet.
- **Also corrected the `JAVA_HOME` guidance.** It suggested
  `JAVA_HOME="<jdk21>" cmd /c mvnw.cmd test`; in Git Bash that mangles a
  Windows path and fails with "JAVA_HOME ... not defined correctly". The note
  now says to check the machine's existing value first (the local JDK has moved
  to `21.0.13.9` and `JAVA_HOME` is already correct) and to use PowerShell if it
  genuinely needs overriding.
- **Full suite verified:** `./mvnw -o clean test -Pintegration` → **33/33, 0
  failures, 0 skipped**, `BUILD SUCCESS`. Both integration tests now *execute*
  (`PipelineExecutorIntegrationTest` 235 s, `PipelineExecutorTimelapseIntegrationTest`
  63 s) rather than skipping — the fixture relocation under
  `test_data/{single,multiple}_timepoint*/` is fully resolved.
- Still outstanding (unchanged by this pass, logged for visibility): CI never
  runs `-Pintegration` (`maven.yml` has no reference to it), so the two tests
  above only ever run locally; no release tag since `v3.4.29` against a current
  `3.13.9`; `CITATION.cff` `date-released` is still the `2019-01-01` placeholder;
  the "Suggested sequencing & milestones" section still calls M1/M2 "the
  immediate next steps" with M1–M7 done; and `.gitignore`'s
  `test_data/GIANI*_Output/` does not match the now-nested
  `test_data/<fixture>/GIANI-*_Output*/` directories, which show as untracked.
- Docs-only; no code change. Version bumped `3.13.8` → `3.13.9` (`docs`), kept
  in sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-05 — Removed the stale NetBeans `.form` files and `//GEN` markers

- The eight NetBeans `.form` files under `ui/` had drifted from the Java
  sources (last edited 2026-10-25, vs 23 UI `.java` commits since October).
  `GIANIUI.form` still contained the seven
  `buildPreviewProcess(...).getPropLabels()` calls behind the 2026-11-01 GUI
  startup crash (the `.java` uses `getStagePropLabels`), and had no Advanced
  Options button. Opening `GIANIUI` in the NetBeans designer would therefore
  have regenerated `initComponents()` from the stale form, silently
  reintroducing the crash and dropping the button. The `.java` files had been
  the real source for some time; `AGENTS.md`'s "edit only through the form"
  rule was actively misleading.
- Deleted all eight `.form` files and stripped the 104 NetBeans markers
  (`//GEN-BEGIN`/`END:initComponents`, `//GEN-BEGIN`/`END:variables`,
  `//GEN-FIRST`/`LAST:event_*`, the `<editor-fold>` wrappers) plus the
  "WARNING: Do NOT modify … regenerated by the Form Editor" Javadoc, via a
  scripted `perl -pi` pass (not `sed -i`, which rewrote `pom.xml` line endings
  earlier). Code otherwise unchanged; `initComponents()` and its GridBag layout
  code are kept as plain Swing.
- `AGENTS.md`: replaced the NetBeans-forms section with "plain hand-maintained
  Swing", explaining why the forms were removed and that label text doubles as
  the `properties.xml` key.
- Version bumped `3.13.9` → `3.13.10` (`chore`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-06 — Closed the CI integration gap (A2.4); three more AGENTS.md fixes

**AGENTS.md fixes.**

- **The version-bump rule contradicted itself, in the drift-causing direction.**
  "Versioning" correctly listed four files (`pom.xml`, `CITATION.cff`,
  `docs/conf.py`, `AGENTS.md`), but "Documentation conventions" and "What to
  verify after changes" both said *three* and omitted `AGENTS.md` — the one file
  whose staleness we keep having to fix. Both corrected to four.
- **The `TrackingPanel` note was wrong after 3.13.10.** It said the panel "has no
  `initComponents()`-style generated layout", but `TrackingPanel.java:70` defines
  `initComponents()`, and now that the `.form` files are gone all nine UI classes
  are hand-maintained with one. Reworded so the historical point survives without
  implying a distinction that no longer exists.
- **"Integration tests silently skip" was stale framing.** Skipping is now the
  exception, not the norm; the bullet documents the new `requireFixtures` flag and
  records the current 33/33-zero-skips baseline so a future skip reads as a
  regression.

**CI integration gap (Phase A2 item 4).**

- `maven.yml` gains an `integration` job (`needs: build`, `timeout-minutes: 45`)
  running `./mvnw --batch-mode -Pintegration -Dgiani.requireFixtures=true verify`,
  so the end-to-end tests run on every push instead of only by hand. Added a
  `workflow_dispatch` trigger and an `always()` upload of
  `target/surefire-reports/` so a CI failure can be diagnosed without a re-run.
  Kept as a *separate* job so `build` still gives fast feedback.
- New `net.calm.giani.TestFixtures.require(path)` replaces the three copies of
  `Assumptions.assumeTrue(fixture.isFile(), ...)`. Default behaviour is unchanged
  (skip, so a developer without the 33 MB images can still run the suite); with
  `-Dgiani.requireFixtures=true` a missing fixture **fails**. Wired through
  surefire `<systemPropertyVariables>` with a `false` default in `<properties>`
  rather than relying on implicit command-line propagation.
- `TestFixturesTest` (3 tests) guards the guard — a helper that silently failed
  open is precisely what let the relocation go unnoticed.
- **Both behaviours verified by experiment**, not by reading: with the CHO
  fixture temporarily moved aside, `-Dgiani.requireFixtures=true` gave
  `BUILD FAILURE` / "test fixture not present ... so this fails rather than
  skips" (exit 1), and the same run without the flag gave `BUILD SUCCESS` with
  `Skipped: 1` (exit 0). Fixture restored; `git status` clean.
- `./mvnw -o clean test` → 34/34, 0 skipped. The CI-equivalent
  `-Pintegration -Dgiani.requireFixtures=true verify` was also run locally.
- **Not yet proven on a real runner.** The job has never executed on
  GitHub-hosted hardware; the unknowns are the 4 GB surefire heap on a
  16 GB runner and whether the ~5 min local runtime stays inside the tests'
  own `@Timeout` (15 and 25 min) when the runner is slower. Watch the first
  push.
- Still outstanding, unchanged: no tag since `v3.4.29` (now against `3.13.11`);
  `CITATION.cff` `date-released` is still the `2019-01-01` placeholder; the
  milestone section still calls M1/M2 "the immediate next steps" (the phrase
  wraps across a line, which is why greps keep missing it); `.gitignore`'s
  `test_data/GIANI*_Output/` still does not match the nested
  `test_data/<fixture>/GIANI-*_Output*/` dirs, five of which remain untracked.
- Version bumped `3.13.10` → `3.13.11` (`chore`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-06 — CI integration job verified on a real runner; job now self-evidencing

- **First real run (42213967190, 3.13.11) was green: 36 tests, 0 skipped**, both
  integration tests genuinely executed. `build` 1m 43s, `integration` 1m 59s.
- **Correcting the previous entry's caveat.** That entry warned the 4 GB heap and
  the per-test `@Timeout`s might not survive a slower runner. The opposite is
  true: the runner is **4–6× faster** than this local Windows checkout on the
  segmentation-heavy tests — `PipelineExecutorIntegrationTest` 59.5 s on CI vs
  229–389 s locally, `PipelineExecutorTimelapseIntegrationTest` 20.4 s vs
  71–100 s. The 15- and 25-minute `@Timeout`s use ~7% of their budget; no change
  needed. Per-push cost is ~2 min, so there is no reason to narrow the trigger.
- **Lesson worth keeping: the 1m 59s duration was initially misread as proof the
  tests had *not* run.** The reasoning calibrated runner speed off the `build`
  job (1m 43s), but `build` is almost entirely fixed overhead — 38 MB fixture
  checkout, JDK setup, dependency resolution, compile, package, jacoco — with
  only ~3 s of unit tests inside it, so it says nothing about CPU throughput on
  the heavy tests. Settling it needed the downloaded surefire artifact. Do not
  infer what a CI job did from its wall-clock duration.
- **Hardening that followed from it (the real gap).** `requireFixtures` protects
  against a missing *fixture*, but not against the tagged tests failing to be
  *selected*: if `-Pintegration` ever stopped activating, they would be excluded,
  the job would stay green, and the uploaded reports would look much the same —
  which is exactly why the green tick was ambiguous. The job now has a
  "Verify the integration tests actually ran" step asserting both
  `net.calm.giani.exec.PipelineExecutor*IntegrationTest.txt` reports exist and
  match `Tests run: N, Failures: 0, Errors: 0, Skipped: 0`, emitting
  `::error::` annotations otherwise. Logic tested both ways against the real
  downloaded artifact (passes) and an empty directory (fails as intended).
- Bumped `actions/upload-artifact` `v4` → `v5`; the run logged a deprecation
  warning that v4 targets Node 20. (`actions/checkout` and `setup-java` were
  already on v5.)
- Still outstanding, unchanged: no tag since `v3.4.29` (now against `3.13.12`);
  `CITATION.cff` `date-released` still the `2019-01-01` placeholder; the
  milestone section still calls M1/M2 "the immediate next steps"; `.gitignore`'s
  `test_data/GIANI*_Output/` still misses the nested
  `test_data/<fixture>/GIANI-*_Output*/` dirs. A downloaded
  `integration-surefire-reports/` folder is also sitting untracked in the repo
  root.
- Version bumped `3.13.11` → `3.13.12` (`chore`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-06 — Cleared four stale passages in DEVELOPMENT_PLAN.md

Found while reading the plan to answer "what's next". All four were
present-tense descriptions of work that had already landed — the drift mode this
pairing is supposed to prevent, and which the "treat status markers as
unverified" rule in `AGENTS.md` exists to catch.

- **I1's `Track Timelapse` follow-up still said "a future pass should rename…
  to e.g. Process All Timepoints".** That shipped in `bcfa31a` (3.10.0), with
  the `DeprecatedProps` mapping. Rewritten as done — but deliberately *keeping*
  the open half: the two flags are still independent, so at defaults tracking
  still silently produces nothing. That is what I3 Step 2 closes, and it should
  not disappear just because the rename happened.
- **I1's problem statement still described `addProcess()`/`removeProcess()` and
  the `checkIfStarDist` hack as current code.** All deleted (`f63b97b`,
  `78d61b6`). Recast as "Original problem statement (2026-10-18) — resolved;
  kept for rationale", with the three refactor goals marked ✔ and pointing at
  what actually implements each. Flagged on 2026-10-31 and survived two passes
  before this one.
- **The "wizard help buttons" known issue told the reader to edit
  `GIANIUI.form`** — impossible since 3.13.10 deleted the `.form` files. Now
  points at `getHelpURI(...)` in `GIANIUI.initComponents()`, and says why the
  old instruction is void.
- **"Suggested sequencing & milestones" still ended "M1 and M2 are the immediate
  next steps"** with M1–M6 long done. Replaced with a dated status: M7 (Phase I)
  is the only milestone left, broken down by I1/I2/I3 with I3's step order, and
  noting the deferred "Known issues" sit outside the milestones. This phrase
  wraps across a line, which is why repeated greps for it kept missing it —
  worth remembering when auditing.
- Docs-only; no code change. Version bumped `3.13.12` → `3.13.13` (`docs`), kept
  in sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-06 — CLI now exits non-zero on failure (deferred known issue)

Every headless path previously exited `0`, so an HPC scheduler reported failed
jobs as successful — the worst of the deferred known issues, because the
failure is invisible exactly where runs are unattended.

- **Restructured for testability.** `main` now delegates to
  `GIANI.runHeadless(String[])`, which *returns* `EXIT_SUCCESS` (0) or
  `EXIT_FAILURE` (1) instead of calling `System.exit` internally; `main` makes
  the single exit call. `init` returns a boolean. This is what let the failure
  paths be unit-tested at all — previously any test would have killed the JVM.
- **Pipeline failure is now detectable.** `PipelineExecutor` is a `Thread`, so
  an exception escaping its `run()` only reached the default handler and
  `join()` returned normally — the caller could not tell the pipeline had died.
  `GIANI.run(Properties)` now installs an uncaught-exception handler and returns
  `boolean`. (Signature changed from `void`; source-compatible for the existing
  statement-position callers in `GIANIMacroExecutor` and the tests.)
- **`Error` vs `Exception`.** The compiler caught that `GenUtils.logError` takes
  an `Exception`, not a `Throwable` as `AGENTS.md` claimed. That matters here:
  `OutOfMemoryError` is a realistic escape for 3D segmentation at `-Xmx4g`, so
  a non-`Exception` is wrapped rather than narrowed. `AGENTS.md` corrected.
- **Unreadable properties file is now fatal.** It was logged and the run
  continued with *default* parameters — producing plausible-looking but wrong
  output, which is worse than failing.
- **Validation early-exits also return non-zero.** The plan left this open
  ("keep `0` … only if that is the documented contract"); there was no such
  documented contract, and a scheduler should see a bad invocation as a failed
  job. Documented in `AGENTS.md` instead.
- `GianiExitCodeTest` (6 tests) pins each failure path: missing properties,
  missing input, job list without a job number, absent job list, job number not
  present in the list (the `getJobDetails` → `null` → NPE path), and an
  unparseable properties file. `./mvnw clean test` → **40/40, 0 skipped**.
- Drive-by: the `-h` text and the `main` Javadoc pointed at the retired GitHub
  wiki; both now point at ReadTheDocs. This is part of the "help buttons point
  at the retired wiki" known issue — **the wizard panel URIs are still
  unfixed**, as is the separate bug that a lone `-h` is never parsed.
- **Deliberately left open and recorded under Known issues:** errors that
  `PipelineExecutor` catches and logs internally (notably "Failed to save
  results file") still do not mark the run as failed, so a job that writes no
  CSV can exit `0`. Per-file skips inside a directory batch *should* stay
  non-fatal; whether writing the primary output is a success condition is a
  separate decision.
- Version bumped `3.13.13` → `3.13.14` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-06 — I3 Step 2: tracking panel redesigned

The panel exposed raw storage: `Specific Timepoint = -1` and
`Linking Distance = -1` sentinels, a 0-based timepoint index, and two
independent toggles whose default combination (`Track Lineage=true`,
`Process All Timepoints=false`) analysed `t=0` only and silently produced no
tracking output.

- **Controls now:** *Timepoints to Analyse* (*First timepoint only* / *All
  timepoints* / *A single timepoint*) with a 1-based *Timepoint* field enabled
  only for the third; *Track Lineage*, available only when analysing all
  timepoints; *Linking Distance* (*Automatic* / *Specify below*) with a *Max
  Linking Distance* field and a units suffix taken from `UNITS`; and
  `Max Frame Gap` renamed *Max Missing Timepoints*.
- **Storage is unchanged.** `PROCESS_ALL_TIMEPOINTS`, `TIME_SELECT` (still
  0-based, `-1` unset) and `TRACK_LINKING_DISTANCE` (`-1` automatic) keep their
  meanings, so existing parameter files, the headless CLI and
  `Ext.GIANI.setProperty` are untouched. Only the renamed key needed a
  `DeprecatedProps` entry.
- **Two mechanism findings drove the design** (both now in
  `DEVELOPMENT_PLAN.md` I3 Step 2):
  - The new controls are deliberately **unbound** from `PropertyExtractor` —
    their labels do not call `setLabelFor`, so it ignores them. Binding them
    would not work anyway: `PropertyExtractor` recurses into a panel *after*
    calling its `update()`, so a raw read of a bound control always clobbers
    what `update()` just derived. Keeping them unbound also means none of the
    friendlier vocabulary leaks into `properties.xml` — no new keys at all.
  - **Disabling *Track Lineage* does not close the trap.** `PropertyExtractor`
    harvests disabled controls too (its `isEnabled()` guard is commented out in
    the library), so a disabled-but-selected toggle would still write
    `Track Lineage=true`. The panel *deselects* it whenever the mode cannot
    produce tracking, in both `update()` and `setVariables()`, so the correction
    applies whether or not the user ever visits the panel.
- **Deliberately not changed:** the shipped defaults still pair
  `PROCESS_ALL_TIMEPOINTS=false` with `TRACK_LINEAGE=true`. The GUI reconciles
  them at run time, but flipping the stored default would silently disable
  tracking for headless/HPC users who set only `PROCESS_ALL_TIMEPOINTS`.
- `TrackingPanelMappingTest` (19 tests) pins the mapping in both directions,
  including the round trips, the sentinel handling, the 0↔1-based conversion,
  and that the legacy default combination can no longer request tracking. The
  mapping is pure static methods so it is testable without a GUI.
- `./mvnw -o clean test` → **59/59, 0 skipped**.
- Version bumped `3.13.14` → `3.14.0` (`feat`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-06 — 3.14.0 live test passed; deprecated-key migration bug logged

Built and installed 3.14.0 with `bin\install-to-fiji.cmd`. It replaced
`giani-3.9.0.jar` in `plugins/`, `IAClassLibrary-v2.5.0.jar` was already in
`jars/`, and there were no duplicates or stale CALM jars. The headless smoke
test loaded `GIANI`, `GIANIUI` and `GIANIMacroExecutor`. A live timelapse run on
the CHO fixture (all timepoints, lineage tracking, manual linking distance 20)
wrote `test_data/multiple_timepoints/GIANI-3.14.0_Output/`.

- **The redesign held.** Time 0–9 across 225 rows; a `Track ID` column; 8
  lineages (ids 2–9) in `GIANI_tracks.csv`; Cell, Cytoplasm and Nucleus track
  masks; `Linking Distance=20` round-tripped; `Max Frame Gap` stored as the
  renamed `Max Missing Timepoints=2`. The check that mattered most passed:
  `properties.xml` contains none of the new UI vocabulary (`Timepoints to
  Analyse`, `Timepoint`, `Max Linking Distance`), so the unbound-control design
  left the stored format untouched and the CLI and macro API are unaffected.
- **Found: `properties.xml` still held `Track Timelapse=true`**, the key renamed
  in 3.10.0. `updateDeprecatedProps` copies the old value onto the new key but
  never removes the old one. Probed against the real classes: a file with both
  `Track Timelapse=true` and `Process All Timepoints=false` reloads with
  `Process All Timepoints=true`, so the stale key silently overrides the user's
  explicit choice, and the file never heals because the stale key is rewritten
  on every save. This predates I3 and affects every `DeprecatedProps` entry;
  Step 2 enlarges the impact because *Track Lineage* is now gated on the mode.
  **Logged only, not fixed**, as the maintainer chose. Recorded under "Known
  issues" with the intended fix (remove the deprecated key after migrating it,
  plus tests for both a legacy-only file and a file holding both keys).
- **Not covered:** every lineage has `parent_track_id = -1`, so no division
  occurred and the 3.8.0 division re-labelling path was not exercised.
- **Log accuracy:** the 2026-11-03 entry says 3.13.6 was installed into this
  Fiji, but `giani-3.9.0.jar` was what was there. That install evidently went
  elsewhere or was rolled back; nothing depended on it. *(Corrected
  2026-11-12: neither — the two installs were into different Fiji instances on
  different machines. See that day's entry.)*
- Docs-only; no code change. Version bumped `3.14.0` → `3.14.1` (`docs`), kept in
  sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-12 — Log housekeeping: two-machine install record; three bugs tracked

- **Corrected the 2026-11-06 "log accuracy" note.** It concluded the 2026-11-03
  install of 3.13.6 "went elsewhere or was rolled back", because the 3.14.0
  install found `giani-3.9.0.jar` in its Fiji. Checked directly: the Fiji at
  this machine's `FIJI_DIR` still holds the untouched `giani-3.13.6.jar` from
  2026-11-03 (and `IAClassLibrary-v2.5.0.jar` in `plugins/`), and its updater
  database has no GIANI entries, so the Fiji updater did not overwrite it
  either (a hypothesis raised and then disproved in this session). The 3.14.0
  install and live test ran in a **different Fiji on a different machine**
  (consistent with the 3.13.9 note that the old hard-coded path did not exist
  on the machine used then). Both log entries were accurate about their own
  machine. Practical consequence: this machine's Fiji is still on 3.13.6, and
  each machine's `bin\local-env.cmd` decides which Fiji `install-to-fiji.cmd`
  targets — always say which machine a live test ran on.
- **Tracked three bugs in `DEVELOPMENT_PLAN.md` "Known issues".** Found on
  2026-11-03 but until now recorded only in that day's log entry, so they were
  invisible to anyone reading the plan for what's next. Each re-verified
  against current (3.14.1) source:
  - StarDist probability/overlap labels swapped in `MaximaFinderPanel`
    (`:379`/`:390`), so saving swaps the two values;
  - `TrackMaskWriter` relabels spot masks with the cell → track map,
    producing meaningless `*_Spot_track_masks`;
  - `GianiDefaultParams.initialise()` stores three per-channel prefix
    constants as keys, leaving junk unsuffixed entries in `properties.xml`.
- Docs-only; no code change. Version bumped `3.14.1` → `3.14.2` (`docs`), kept in
  sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-12 — Deprecated-name migration: remove the old key; current name wins

Fixes the known issue logged on 2026-11-06 (3.14.0 live test).

- **Bug.** `updateDeprecatedProps` copied each deprecated key's value onto its
  current name but never removed the deprecated key, so it was saved back into
  every `properties.xml`. And because it ran *after* the file had been merged
  over a defaults-populated `GianiDefaultParams`, it could not tell a user's
  explicit current-name value from a default: a stale key always won. A file
  holding `Track Timelapse=true` and `Process All Timepoints=false` reloaded as
  `true`, and never healed.
- **Why removing the key alone was not enough.** The plan's to-do was "remove
  the deprecated key after migrating it". That heals the file, but only after
  the stale value has already overridden the user's choice once. The precedence
  had to be fixed too.
- **Fix.** `GianiDefaultParams.loadProperties` now loads the file into an empty
  `Properties`, migrates *that*, merges it over `props`, then runs
  `DeprecatedProps.mapControlChanges` on the merged result (as before).
  `updateDeprecatedProps` now migrates in place: a deprecated-only key moves to
  its current name; when both are present, the current name is kept; the
  deprecated key is always removed.
- **New hazard, guarded.** Migration now deletes every key that starts with a
  deprecated name, so a current key that happened to extend an old name would
  vanish on every load. `noCurrentKeyStartsWithADeprecatedName` checks every
  current key (including the per-channel spot keys for all 10 channels) against
  every deprecated name. `AGENTS.md` documents the rule.
- **Tests** (`GianiDefaultParamsTest`): legacy-only file (old key removed),
  file holding both keys (current wins), per-channel suffixed name, and the
  guard above. Run against the old code first: three of the four failed,
  including the exact reported bug (`expected: <false> but was: <true>`).
  - Unexplained, not pursued: under the old code the per-channel case also read
    back `true` rather than the migrated `false`. The old code should have
    copied the value, so the `true` presumably comes from inside IAClassLibrary's
    `PropertyWriter.loadProperties` when loading into a defaults-populated
    object. The new code loads into an empty `Properties` and behaves
    correctly; the test pins it against the real `PropertyWriter`.
- `./mvnw clean test` → **62/62, 0 skipped**.
- Version bumped `3.14.2` → `3.14.3` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-12 — StarDist probability/overlap labels un-crossed

Fixes the first of the three bugs tracked on 2026-11-12 (3.14.2).

- **Bug.** In `MaximaFinderPanel.initComponents()` the probability label was
  given the overlap key's text and the overlap label the probability key's text,
  while each text field was initialised from its own, correct, key.
  `PropertyExtractor` writes a field's value under the key named by its label, so
  the first collect of panel state stored each value under the other's key.
- **Who it affected.** (a) Anyone running StarDist (beta) at untouched defaults:
  the pipeline used probability 0.6 / overlap 0.75 instead of the documented
  0.75 / 0.6, and the on-screen values sat beside the wrong labels. (b) The
  per-channel spot panels, which are built *after* parameters may have been
  loaded, so a user's saved per-channel thresholds were swapped on first
  collect. Values a user typed into the panel after it was built were stored
  consistently with the labels they saw, so those were unaffected.
- **Fix.** The two `setText` arguments are swapped back (two lines). The on-screen
  row order changes as a result: Probability now sits above Overlap.
- **Behaviour change to expect.** StarDist runs at default settings will now give
  different results from earlier versions, because the defaults are finally
  applied the way they were documented. A `properties.xml` saved by an older
  version holds the values the pipeline actually used, keyed correctly for the
  pipeline, so reloading one reproduces that older analysis (reasoning from the
  key semantics; not checked against a real StarDist run).
- **Audited the sibling pairs.** Checked every label → key and field → key pairing
  in `MaximaFinderPanel` (all 15 text-field parameters). Only the StarDist pair was
  crossed. Other panels were not audited.
- **Tests** (`MaximaFinderPanelStarDistTest`, 5): the two thresholds and the
  shipped defaults survive a collect, for the nuclear and per-channel panels; and
  a broad guard that gives every text-field parameter a distinct value and checks
  each survives a collect, so any future crossed pair is caught. Run first against
  the unfixed code: the three StarDist-specific tests failed with the values
  trading places (`0.75` → `0.6`, `0.88` → `0.22`). The broad guard was added
  after the fix, so it was checked separately by temporarily reverting the fix;
  all 5 then failed, naming the crossed parameter.
- `./mvnw clean test` → **67/67, 0 skipped**. The panel builds headless, so the
  tests run on CI without a display.
- Not verified live: no StarDist environment was run, only the panel's
  property round trip. A live check that both values survive a save/load in the
  real wizard is still worth doing.
- Version bumped `3.14.3` → `3.14.4` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — AGENTS.md accuracy fixes (3.14.5)

- Reviewed `AGENTS.md` against source and fixed four stale or contradictory
  statements:
  - The headless gotcha still said `System.exit(0)` on completion and on
    early-exit error paths, contradicting the entry-point notes. Since 3.13.14
    there is a single `System.exit(runHeadless(args))` exiting `0`/`1`; the
    gotcha now says so and that new failure paths should return
    `EXIT_FAILURE` rather than exit.
  - Removed the pinned test counts ("36 tests" in the CI timings, "33/33" in
    the fixture bullet), which disagreed with each other and predated several
    test classes. The zero-skip rule is kept; counts are left to this log.
  - `DebugTools.setRootLevel("WARN")` is called in three places, not two —
    added the `GIANIUI` constructor.
  - The integration job's "~5 min" is now "~2 min on CI, ~5 min locally",
    matching the measured 1m 59s.
- No code change; `DEVELOPMENT_PLAN.md` has no item affected.
- Version bumped `3.14.4` → `3.14.5` (`docs`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — Working agreement adapted to GIANI (3.14.6)

- `WORKING_AGREEMENT.md` and `.claude/settings.json` had been copied from a
  Python/pixi project. Replaced the pixi and `.py` specifics with GIANI's:
  - Agreed with Dave: Claude may edit Java under `src/`, edit `pom.xml` (the
    version bump; dependency changes still need a "yes"), and run the unit
    tests (`./mvnw clean test`, `-Dtest=…`) without asking.
  - Ask first: `-Pintegration` runs, `bin\install-to-fiji.cmd` /
    `bin\run-fiji.cmd`, `.mvn/`, `bin/local-env.cmd`, dependency changes.
  - Never hand-edit `assets/` (LFS) or `test_data/` fixtures (also `deny` rules).
  - WebFetch allow-list switched from PyPI to Maven Central, JitPack, SciJava
    javadoc, imagej.net and giani-docs.
  - Generic sections 1–4 now point to §5 for project specifics, so they stay
    copyable.
- `AGENTS.md` now points to `WORKING_AGREEMENT.md` (nothing loaded it before),
  and its "push at the end of each session" rule is reconciled with "Dave commits
  and pushes": Claude flags unpushed work rather than pushing.
- `.gitignore`: added `.claude/settings.local.json`.
- No code change; `DEVELOPMENT_PLAN.md` has no item affected. Not run: the new
  permission patterns were checked by reading only, not by triggering each one.
- Version bumped `3.14.5` → `3.14.6` (`chore`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — Wizard help links repointed to ReadTheDocs (3.14.7)

- The nine wizard help buttons linked to the retired GitHub wiki. They now open
  the matching `docs/` page on `giani-docs.readthedocs.io/en/development/`.
  `GIANIUI` holds one `DOCS_BASE_URL` plus a `HELP_*` constant per page, and
  `getHelpURI(page)` builds the URL (now `static`).
- **Why `development`, not `latest`:** checked the live site. `latest` (the RTD
  default, where the bare root redirects) is a stale build of the retired
  giani-docs repo, with `docs/`-prefixed paths and no tracking page; the new
  paths 404 there and on `stable`. Master has no `docs/` folder, so
  `development` is the only current build. Switching later is a one-line change.
- **Tests** (`GIANIUIHelpPagesTest`, 3): every `HELP_*` page has a
  `docs/<page>.rst` source; each URI is `DOCS_BASE_URL + page + ".html"`; and
  `GIANIUI.java` contains no wiki link.
- `./mvnw clean test` → **70/70, 0 skipped**. Not checked in the running GUI
  (the help buttons were not clicked in Fiji).
- Still open (recorded in the plan): the stale `latest` default on RTD, and no
  help link on `TrackingPanel`.
- Version bumped `3.14.6` → `3.14.7` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — `GIANI -h` prints usage (3.14.8)

- A lone `-h` was never parsed: the flag loop stops at `args.length - 1`
  because it reads each value from `args[i + 1]`, so the run fell through to
  "No valid properties file specified" and exited `1`.
- `runHeadless` now checks for `-h` or `--help` anywhere in the arguments before
  the loop, logs a flag summary (`GIANI.USAGE`) and returns `EXIT_SUCCESS`
  without validating or running anything. Help takes priority over other
  arguments (the usual convention). The old in-loop `-h` case, which only
  logged a docs link and carried on, is removed.
- `docs/cluster/index.rst`: `-h` is back in the parameter list (it had been
  removed until fixed), with a note that the Windows FIJI launcher may not
  show the text, as for progress messages.
- **Tests** (`GianiExitCodeTest`, +4): lone `-h` and `--help` exit `0`; `-h`
  with an otherwise-failing invocation still exits `0`; `USAGE` mentions every
  flag. The lone-`-h` test fails against the old code (it returned `1`).
- `./mvnw clean test` → **74/74, 0 skipped**. Not run through the real Fiji
  launcher.
- Version bumped `3.14.7` → `3.14.8` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — Plan: Phase J, release & discoverability

- Added **Phase J** and milestone **M8** to `DEVELOPMENT_PLAN.md`, at Dave's
  request: once the release is ready, make GIANI easy to find when people ask
  search engines or AI assistants questions like "Can I analyse 3D cells in
  ImageJ/Fiji?". Covers GitHub metadata (description, topics, website, social
  preview, a proper Release), a README rewritten around those questions,
  listings outside GitHub (update site, imagej.net, image.sc, bio.tools / BIII,
  Zenodo DOI, RTD default version), and a before/after check.
- Noted while drafting: GIANI still ships on the **CALM** update site, which
  users won't find by searching for "GIANI", although CALM is retired. Left as a
  decision in J3.
- Plan only, no code change, so no version bump.

## 2026-11-26 — No more bare per-channel keys in `properties.xml` (3.14.9)

- `GianiDefaultParams.initialise()` set `BLOB_CHAN_SELECT_LABEL`,
  `BLOB_CHAN_NOISE_TOL_LABEL` and `BLOB_CHAN_RAD_LABEL`, which are per-channel
  *prefixes* ending in `"Channel "`, so every saved `properties.xml` held junk
  entries such as `Radius for Simple Spot Detection in Channel =0.0`.
- Checked first: nothing reads the unsuffixed keys. `PipelineBuilder`,
  `GIANIParamInfos` and the UI always append a channel number, and
  `PipelineBuilder.setDefaultFociParams` gives the numbered keys the same
  defaults (`0`, `0.0`, `0.0`). The three lines are removed, so behaviour is
  unchanged.
- **Healing old files:** existing files carry the junk (older ones under
  deprecated names, which migration maps onto the current prefixes), and
  loading one would write it straight back. `loadProperties` now drops any key
  ending in `"Channel "` from the loaded file, after migration and before the
  merge, the same pattern 3.14.3 used for stale deprecated keys. Every
  `"Channel "`-ending constant is a per-channel prefix, and a test pins that no
  complete key ends that way.
- **Tests** (`GianiDefaultParamsTest`, +2): fresh defaults plus all ten
  channels' spot defaults hold no bare key; a file holding the three bare keys
  plus a deprecated bare key loses them all on load, while a numbered value
  (`… in Channel 1` = `2.5`) survives. Both fail against the old code by
  construction (not re-run against it).
- `./mvnw clean test` → **76/76, 0 skipped**. Integration tests not run: they
  only check that `properties.xml` exists, and no numbered key or value changed.
- `AGENTS.md`: per-channel section now says never to store a bare prefix, and
  that a new non-per-channel key must not end in `"Channel "`.
- Version bumped `3.14.8` → `3.14.9` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — Track masks no longer made from spot masks (3.14.10)

- `TrackMaskWriter.write` relabelled every `*_masks` folder with the cell label
  → track map, including `*_Channel_N_Spot_masks`. Spot masks are labelled by
  spot index, so the resulting `*_Spot_track_masks` were meaningless (seen in
  the 3.13.6 run).
- The writer now only processes folders ending `_Nucleus_masks`, `_Cell_masks`
  or `_Cytoplasm_masks` (`TRACKABLE_MASK_SUFFIXES` / `isTrackableMaskDir`).
  Those three share the cell label space because the cell watershed uses
  `remap=true`. Suffixes checked against real output folders in `time_test/`
  and `docs/complete/index.rst`. The explicit allow-list also excludes the
  writer's own `*_track_masks` output, which the old filter had to special-case.
- **Tests** (`TrackMaskWriterTest`, +2): folder classification (the three
  object types are trackable, spot and track-mask folders are not); and an
  end-to-end `write` over a temp dir with real PNGs, checking the cell mask is
  relabelled (`10` → `TRACK_LABEL_OFFSET + trackId`) and no spot track folder
  is created. My first version of that test expected the wrong label (it took
  the wrong constructor argument as the track ID); corrected to match the
  existing `buildRemap` test.
- `./mvnw clean test` → **78/78, 0 skipped**. Then
  `-Pintegration -Dgiani.requireFixtures=true` → **80/80, 0 skipped**
  (`PipelineExecutorIntegrationTest` 268 s, `…TimelapseIntegrationTest` 38 s,
  which runs this writer; neither asserts on mask folders). The first attempt
  never ran: unquoted in PowerShell, `-Dgiani.requireFixtures=true` was split at
  the dot (noted in `AGENTS.md`).
- Docs: `docs/start/tracking.rst` now says track masks cover nucleus, cell and
  cytoplasm, not spots; `AGENTS.md` notes the same.
- Version bumped `3.14.9` → `3.14.10` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — A results or tracks CSV that cannot be saved fails the run (3.14.11)

- **Decision (Dave):** failing to write either CSV, the results CSV or
  `GIANI_tracks.csv`, fails the run. Rationale: on HPC the realistic cause is a
  full disk or exceeded quota, nobody reads the logs of hundreds of jobs, and
  schedulers/workflow managers only see the exit code; a run with no CSV has
  produced nothing usable. This completes what 3.13.14's exit codes set out to
  do.
- `PipelineExecutor.saveCsvOutputs` writes both CSVs, attempting the second even
  if the first fails, logs which file could NOT be saved (with its full path),
  and returns whether both succeeded. The tracks CSV is only required when
  tracking produced tracks. The run still writes the remaining outputs (track
  masks, spot-summary annotation, `properties.xml`); those stay log-only. The
  final log line reads `FAILED …` instead of `Done …`. `GIANI.run` returns
  `exec.csvOutputsSaved()`, so headless exits `1`. GUI behaviour is unchanged
  apart from the clearer log messages; the macro path ignores the return value
  as before.
- **Found while testing:** `DataWriter.saveResultsTable` (IAClassLibrary,
  `DataWriter.java:141`) returns without writing anything when the table has
  no rows. So a run that measured nothing has no CSV and still exits `0`, and
  an end-to-end failure test via an unreadable input could not work (nothing is
  written, so a blocked path is never touched). Recorded as an open question in
  the plan; a test pins today's behaviour.
- **Tests** (`PipelineExecutorCsvOutputTest`, 5): both saved → true; results
  path blocked by a directory → false, tracks still written; tracks path
  blocked → false, results still written; no tracks → tracks CSV not required;
  the empty-run behaviour above. Both integration tests now assert
  `GIANI.run(...)` returns `true`, covering the success path on real data.
- Docs: new "Exit status" section in `docs/cluster/index.rst`; `AGENTS.md`
  exit-code note updated.
- `./mvnw clean test` → **83/83, 0 skipped**. Then
  `-Pintegration -Dgiani.requireFixtures=true` → **85/85, 0 skipped**: both
  real-data runs report success through the new check
  (`PipelineExecutorIntegrationTest` 257 s, `…TimelapseIntegrationTest` 38 s,
  the latter writing `GIANI_tracks.csv`).
- Version bumped `3.14.10` → `3.14.11` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-26 — Plan: spot detection on the default path (decided, not implemented)

- Investigated the known issue that the spot panels are hidden unless Advanced
  Options is on, so *Localise Spots* runs with spot radius and threshold `0.0`.
- Probe (scratch program, not in the repo): TrackMate 8 `LogDetector`, called as
  `MultiThreadedMaximaFinder.run` calls it, on a synthetic 64×64×16 stack with
  0.2 µm pixels, Gaussian noise and three ~0.5 µm spots. Radius `0` returned
  **65,536 spots, i.e. every voxel**, at threshold 0 and 50 alike; radius
  0.5 µm returned 626 at threshold 0 and exactly 3 at threshold 50. So the
  default path produces meaningless spot data today. Not checked on real data.
- Decided with Dave (recorded in `DEVELOPMENT_PLAN.md`, Known issues): show the
  spot panels whenever *Localise Spots* is ticked; default radius 0.5 µm; refuse
  to run with a LoG spot radius ≤ 0 (GUI dialog / headless exit `1`). Deferred
  for implementation; plan only, so no version bump.

## 2026-11-27 — Rebuilt the missing `PipelineExecutorCsvOutputTest` (3.14.12)

Found while reviewing the 3.14.x commits: the 3.14.11 entry says it added
`PipelineExecutorCsvOutputTest` (5 tests) and records `clean test` at **83/83**,
but that class was never committed and did not exist on disk, in any ref, or in
a stash. The repo had **78** tests (78 + 5 = 83), reproduced by a fresh
`clean test`. So the failure path 3.14.11 exists to add, `saveCsvOutputs`
returning false when a CSV cannot be saved, had no unit test; only the success
path was covered, by the integration tests' new assertions. The same slip as
`GianiExitCodeTest` being left out of its commit, except that this time the file
was gone entirely.

- **Rebuilt from the 3.14.11 entry's description of the five cases:** both CSVs
  saved → `true`; results path blocked → `false` with the tracks CSV still
  written; tracks path blocked → `false` with the results CSV still written; no
  tracks → tracks CSV not required; and the empty-table behaviour, under the plan's
  name for it, `runThatMeasuredNothingWritesNoCsvAndStillSucceeds`. These are
  re-creations from a description, not the lost file, so the originals' exact
  assertions are unknown.
- **A CSV is made unwritable by putting a directory at its path**, so the real
  writers throw their real `IOException`; no mocks and no platform-specific file
  permissions.
- **The empty-table test pins today's library behaviour, not a decision:**
  IAClassLibrary's `DataWriter.saveResultsTable` returns without writing when
  there are no rows (`DataWriter.java:141`, checked in the v2.5.0 sources), so a
  run that measured nothing writes no CSV and is reported as saved. The plan's
  open question on this is unchanged; the test should change with it.
- **Checked against a mutation, not just run.** With the two
  `saved = false;` lines in `saveCsvOutputs` temporarily disabled, exactly the
  two failure-path tests failed (`expected: <false> but was: <true>`) and the
  other three passed; the lines were then restored and `git diff -- src/main`
  confirmed empty.
- **Correcting the record:** the 3.14.11 entry's `83/83` was not reproducible
  at that commit, and its follow-up `85/85` for `-Pintegration` (83 plus the two
  integration tests) was too, since the integration suite adds 2 to whatever the
  unit count is. At 3.14.11 the unit suite was 78. The two real-data integration
  runs themselves (257 s and 38 s) are not in question and were not re-run now.
- `./mvnw clean test` → **83/83, 0 skipped** (78 + the 5 rebuilt tests). The
  integration suite was not run.
- Version bumped `3.14.11` → `3.14.12` (`test`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-27 — Spot detection: visible panels, a positive default radius, and refusing a zero radius (3.14.13)

Implements the three decisions Dave recorded on 2026-11-26 (see "Known issues" in
`DEVELOPMENT_PLAN.md`). Background: with a radius of 0, TrackMate's `LogDetector`
returns every voxel as a spot, so *Localise Spots* on the default path ran with a
hidden `0.0` radius and produced meaningless spot data.

- **Panels on the default path.** `GIANIUI.addAdditionalBlobDetectionPanels` now
  also slots the per-channel spot panels (with Preview) into `corePanels`, right
  after the measurement panel, via a small static `GIANIUI.insertAfter` so the
  ordering is testable without a `JFrame`. They are removed again when the
  channels change or *Localise Spots* is unticked. The colocalisation panel was
  not part of the decision and stays advanced-only.
- **Default radius 0.5 µm** (`PipelineBuilder.DEFAULT_SPOT_RADIUS`), applied with
  `putIfAbsent`, so a saved file keeps its own value and old analyses stay
  reproducible. The threshold default is unchanged. The tooltip now says the
  radius must be greater than 0.
- **Refusal.** `PipelineBuilder.spotRadiusProblems` is shared by the wizard's
  `validateInput()` (a dialog) and by `GIANI.run` (logs `ERROR:` and returns
  false, so the CLI exits `1`; the macro API gets the check too). It names the
  channel and the on-screen label, which is also the `properties.xml` key.
- **The detector is decided by four flags, not the method string.** I read
  `MultiThreadedMaximaFinder.run` in the v2.5.0 sources: threshold, then Hessian,
  then StarDist, then ilastik, and the LoG detector only if none is set. The
  `METHOD` string and `BLOB_DETECT` play no part. The plan said "for each detector
  method", and keying the check on the method string would have been wrong, so
  `usesSimpleSpotDetector` mirrors the flags. It must be revisited when the
  IAClassLibrary pin changes.
- **Interpretation to confirm:** a missing, blank, non-numeric or non-finite
  radius is also refused, not only one ≤ 0. The decision said "≤ 0"; a
  hand-written file lacking the key would otherwise fail later with the
  library's "Missing value" error, which is a worse message for the same
  outcome. Easy to narrow if you disagree.
- **Finding:** `setDefaultFociParams` has exactly one production caller, the
  GUI. A headless run never applies per-channel defaults, so the new default
  reaches users through the wizard and through GUI-saved files, and a
  hand-written properties file without per-channel keys already failed loudly.
- **Test-design hazard caught.** The `GIANI.run` refusal test would, if the
  refusal ever regressed, carry on and scan the default input directory, which
  is the user's home folder. Both directories are now empty temp folders, so a
  regression fails harmlessly.
- **Tests (22 new).** `SpotRadiusValidationTest` (14): default and its
  reproducibility, each bad radius form, a case for each of the four other
  detectors (none blocked by a zero blob radius), unselected channels and
  non-localised runs, message content. `GianiSpotRefusalTest` (2): `run()` and
  the CLI refuse and say why, the latter proving the exit status comes from the
  refusal by capturing the log. `GIANIUIPanelOrderTest` (6): the list handling.
  **Checked by mutation, not just run:** with four deliberate breakages
  (`>` to `>=`, Hessian dropped from the detector rule, insert-before-anchor,
  refusal disabled) each was caught by the tests aimed at it and by nothing
  unexpected; all were reverted and `grep MUTATION src/` is empty.
- **Docs.** `measure/localising_spots.rst` no longer says the panels are hidden
  or that defaults are used; it gains a "Spot radius" section. The exit-status
  paragraph in `cluster/index.rst` and the exit-code note and a new gotcha in
  `AGENTS.md` are updated.
- `./mvnw clean test` → **105/105, 0 skipped** (83 + 22). The integration suite
  was not run.
- **Not verified:** the on-screen flow (panels appearing on the default path, the
  dialog) has not been seen in Fiji, and nothing was run on real spot data, so
  how 0.5 µm behaves on real images is untested. Panel ordering is covered only
  through the static helper.
- Version bumped `3.14.12` → `3.14.13` (`fix`; I judged this a fix for silently
  meaningless output rather than a feature, so say if you would rather call it
  `feat`), kept in sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and
  `AGENTS.md`.

## 2026-11-27 — 3.14.13 installed and live-tested in Fiji; the output read against its source file

Installed with `bin\install-to-fiji.cmd` (with Dave's go-ahead): `giani-3.14.13.jar`
replaced 3.14.0 in `plugins/`, `IAClassLibrary-v2.5.0.jar` unchanged in `jars/`,
exactly one GIANI jar and no CALM jars; the headless smoke test loaded all three
entry classes; the manifest reads `Implementation-Version: 3.14.13`. Fiji was not
launched by Claude.

- **Dave's confirmation:** "everything checks out" for the four planned checks:
  the spot panels appearing on the default path, the refusal dialog on a zero
  radius, an old file not being wrongly blocked, and the saved parameters. He
  then confirmed explicitly that the panels on the default path and the refusal
  dialog behaved as intended, having observed them first-hand in the live test.
  They are on-screen behaviour that no output file shows, so Claude did not see
  them itself and has recorded them on his confirmation.
- **What the output folder does show**
  (`test_data/single_timepoint/GIANI-3.14.13_Output`): it is the old-file check.
  The run loaded `GIANI-3.7.6_Output/properties.xml`, which stores a simple-
  detector radius of `0.0` on all three channels but has the **advanced
  (Hessian) detector on** for each, so the refusal correctly did not fire and
  the run completed. This is the real-data case for "other detection methods
  are never blocked by their unused blob radius".
- **Same outcome as the file it came from.** Out of 87 parameters, exactly three
  differ between the 3.7.6 and 3.14.13 files, and they are the three bare
  per-channel prefix keys (`Detect Spots in Channel `, `Quality of Simple Spot
  Detections in Channel `, `Radius for Simple Spot Detection in Channel `) that
  3.14.9 drops on load, so this confirms that healing on a real file. The
  3.7.6 run also found **no spots** (its output has no spot folders and a
  `spot_summary_data.csv` with only a `Cell_Index` column), and so did this one
  (69 main-CSV rows = 23 cells × 3 object types, against 22,893 for the 3.7.4
  run). So the empty spot output reproduces the original run and is not a
  regression.
- **Why no spots (not established):** the loaded Hessian settings are minimum
  radius 0.5 and quality 1000. The 3.7.4 baseline on the same image used the
  *simple* detector at radius 0.5 and found 2,891 / 14,298 / 5,635 spots in
  channels 0 / 1 / 2. (Corrected 2026-11-27: this entry first said that baseline
  used threshold 500 throughout. Its thresholds were 500 for channel 0 and 1000
  for channels 1 and 2.) Because the simple detector found spots at 1000, a
  quality of 1000 is not obviously too strict, and quality scales may not be
  comparable between detectors, so the cause of the empty Hessian output is
  unknown. The detector was not run here.
- **A correction to my own test plan.** Check 4 said any completed run's
  `properties.xml` would show the radius as `0.5`. That is wrong for a run that
  loads a saved file: `putIfAbsent` deliberately keeps the stored value, so this
  output correctly shows `0.0`. The `0.5` default only appears when no value is
  stored, i.e. a fresh session.
- **Was untested on real data at this point:** detection results at the 0.5 µm
  default, since nothing yet had been run from a fresh session with the simple
  detector. (Done later the same day; see the next entry.) The default threshold
  is `0`, which the synthetic probe suggests returns many noise spots, hence the
  Preview.
- **Observation, not acted on:** a run in which spot detection finds nothing in
  every channel completes silently with exit `0`, as this one did. Whether that
  deserves a warning is a separate question; it is not in the plan.
- Docs-only; no code change. Version bumped `3.14.13` → `3.14.14` (`docs`), kept in
  sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-27 — Fresh-session run on real data: the 0.5 default checked, and a 3.14.0 regression found

Dave ran 3.14.13 from a fresh session (no parameters file loaded) on
`test_data/single_timepoint`, with the simple spot detector, and supplied the log
and `GIANI-3.14.13_Output_(1)`. This closes the gap left by the previous entry.

**The 0.5 µm default works on real data.**
- The log reports blobs "5.2 pixels in diameter", i.e. the 0.5 µm radius, and the
  output's `properties.xml` stores `Radius for Simple Spot Detection in Channel N
  = 0.5` for all three channels. The Preview and the full run both found the same
  2,965 blobs in channel 0.
- **Channel 0 is a controlled comparison with the 3.7.4 baseline.** Same
  detector, same radius (0.5), same threshold (500), same image: 2,893 spots
  against 2,891. The 2-spot difference is plausibly the slightly different
  nuclear segmentation (the baseline had top-hat filtering on; this run had it
  off; nuclear volume differs by 1.3%); that attribution is inferred.
- **Channels 1 and 2 are not comparable, and I got this wrong.** They gave
  20,754 and 10,179 spots against 14,298 and 5,635, but the baseline's thresholds
  for those channels were 1000, not the 500 used here. A lower threshold giving
  more spots is the expected direction. I had said the baseline used 500 for all
  channels, because my earlier settings diff filtered out every line containing
  "Spot" and so could not show a per-channel difference; the previous entry has
  been corrected.
- **Cell masks are unchanged**, which rules out segmentation as the cause of the
  gap: total cell volume is 206,649 vs 206,626 (0.01%). The spot rows in the main
  CSV equal the spot-summary totals exactly in both runs (22,824 and 33,826).
- Raw blobs found were 2,965 / 25,144 / 10,602, of which 2,893 / 20,754 / 10,179
  were assigned to cells (97.6% / 82.5% / 96.0%).

**Regression found: `Max Missing Timepoints` is stored blank (introduced in
3.14.0, by Claude).** The log opens with `Max frame gap not a parsable integer -
using default.`, and both 3.14.13 outputs store the parameter as an empty value
where the 3.14.0 test stored `2`.
- **Cause:** the 3.14.0 `TrackingPanel` rewrite replaced `addLabelField(…, value)`
  with `addRow(…)`, which does not set the field's text, and `readFromProps()`
  initialises the timepoint and linking fields but not `maxMissingField`. So the
  field starts empty, and the first collect of panel state (which
  `addAdditionalBlobDetectionPanels` does before anything reads the parameters
  back) writes `""` over the default `2`.
- **Reproduced in isolation** with a throwaway probe against the built classes:
  the default in `props` is `2`; after a WRITE on a freshly built panel it is
  `''`; after a READ then a WRITE (what *Load Parameters* does) it is `2`. That is
  also why the 3.14.0 live test, which loaded a file first, stored `2` and hid it.
  Why the earlier 3.14.13 run, which also loaded a file, stored a blank is not
  something the files let me reconstruct; the probe shows the field starts empty.
- **Effect today:** the executor falls back to its default, so the tracker used
  the same value as before and results are unaffected. The visible consequences
  are the log line, a blank value in every saved `properties.xml`, and an empty
  field on the Tracking panel under Advanced Options.
- **Why the tests missed it:** `TrackingPanelMappingTest` covers the static value
  mapping only, and nothing builds the panel and checks what a collect writes, the
  gap `MaximaFinderPanelStarDistTest` closed for another panel.
- **Not fixed here.** The fix is one line plus a round-trip test; recorded under
  "Known issues" in `DEVELOPMENT_PLAN.md` pending Dave's go-ahead.

- Docs-only; no code change. Version bumped `3.14.14` → `3.14.15` (`docs`), kept in
  sync across `pom.xml`, `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-27 — `Max Missing Timepoints` no longer stored blank (3.14.16)

Fixes the regression recorded in the previous entry (introduced in 3.14.0).

- **Fix.** `TrackingPanel.initComponents()` now sets `maxMissingField`'s text from
  `props`, as the pre-3.14.0 `addLabelField(…, value)` did. Without it
  `PropertyExtractor` only ever read the field (after a load), so the first collect
  of a fresh session wrote `""` over the stored value.
- **Set once at construction, not in `readFromProps()`, on purpose.** `update()`
  calls `readFromProps()` whenever the panel is navigated to, so putting it there
  would have made this field lose uncollected edits too (see below).
- **Test written first and seen to fail.** `TrackingPanelStateTest` (5) builds the
  panel, places it in a parent container and collects it with `PropertyExtractor`,
  the way the wizard does. Against the unfixed code all 5 failed with `expected:
  <X> but was: <>`. That included a saved `5` and `3`, so the field lost any value
  it was built from, not only the default. With the fix: `./mvnw clean test` →
  **110/110, 0 skipped** (105 + 5). Red then green is the check that the tests
  detect the bug; no separate mutation run was needed.
- **Not re-run in Fiji.** The fix is covered by the unit tests and by the earlier
  isolated probe's scenario; it has not been seen in the wizard.
- **Found while fixing, not fixed: edits to the Tracking panel's other controls
  are discarded on re-entry.** `update()` rebuilds the unbound controls from
  `props`, and nothing collects the panel when you leave it. A probe against the
  built classes (select *Specify below*, type `7.5`, call `update()`) gave
  mode *Automatic* and an empty distance, while `maxMissing` kept its edit, which
  confirms the choice above. Edits survive if a Preview or Run collects first, so
  it bites only someone who goes Next then Previous without previewing. Recorded
  under "Known issues" with two options; it is a design decision, so left for
  Dave.
- **Probe housekeeping.** The first attempt failed because my `clean test` had
  removed the dependency jars from `target/`; the probe was run against Fiji's
  `jars/` folder instead (read-only, same IAClassLibrary v2.5.0).
- **`AGENTS.md`** gains the corollary: a text field must be given its initial
  value when the panel is built, or it writes `""` on the first collect.
- Version bumped `3.14.15` → `3.14.16` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-27 — Tracking panel edits no longer lost on re-entry (3.14.17)

Fixes the second finding from the previous entry, with the approach Dave chose
(collect on leave).

- **Bug.** `GIANIUI.updateLayer` hides every panel and then calls `update()` on the
  one it shows; `TrackingPanel.update()` rebuilds its unbound controls from
  `props`; nothing collected the panel when it was left. So Next then Previous
  silently reset the timepoint mode, linking mode and distance to what `props`
  held. Edits survived only if a Preview or Run collected first, and then the
  value actually used was the stale one.
- **Fix.** `TrackingPanel.setVisible` collects the panel's state when it goes from
  visible to hidden, so `props` is authoritative before `update()` re-reads it.
  Only that transition collects: `updateLayer` hides *every* panel on *every*
  navigation, and a panel that is already hidden must not write stale controls
  over `props`. The Advanced Options toggle hides the panel through the same
  path, so it is covered too. No change to `GIANIUI`.
- **Why an override rather than a call in `updateLayer`:** it keeps the behaviour
  in the panel, where it can be tested without a `JFrame`, and it needs no
  special-casing of one panel in the frame.
- **Tests first, then the fix.** `TrackingPanelNavigationTest` (3). Before the
  fix the main test failed with `expected: <All timepoints> but was: <First
  timepoint only>` and the two guards passed. After: `./mvnw clean test` →
  **113/113, 0 skipped** (110 + 3).
- **The guards were then checked by mutation**, because they passed before the fix
  and so proved nothing on their own. With the "only when leaving" check removed,
  the already-hidden test failed (`expected: <true> but was: <false>`). With
  `update()` made a no-op (the alternative fix, which would let a stale edit
  clobber a *Load Parameters*), the load test failed (`expected: <false> but was:
  <true>`). Both mutants were reverted; `grep MUTATION src/` is empty and the main
  diff against HEAD is only the override.
- **One side effect, by reasoning only:** the GUI constructor's `addPanel` hides
  the freshly built panel, which is still visible at that point, so it collects
  once at startup. That writes the panel's already-normalised state to `props`
  (`Track Lineage` becomes `false` at startup instead of at the first collect; the
  toggle was already deselected at construction). Not seen in the wizard.
- **Other panels:** audited later the same day (next entry). Not re-run in Fiji.
- Version bumped `3.14.16` → `3.14.17` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-27 — Audit: which other panels lose uncollected edits on re-entry

Dave asked for the audit after the Tracking panel fix. It was read-only: no code
and no tests changed, and the findings are recorded here for his decision.

- **Method.** `GIANIUI.updateLayer` is the only caller of `update()`, and six
  panel classes implement it (`TrackingPanel` was already fixed). I read each
  `update()`, then probed each panel with a throwaway class in the scratchpad: load
  the 3-channel fixture (`test_data/single_timepoint`, read-only), build the panel
  as the wizard does, simulate a user edit, make the re-entry `update()` call, and
  read the control back. The probe ran headless against `target/classes` with
  Fiji's `jars/` as the classpath; it was not run in the wizard.
- **Result.** Three controls lose an uncollected edit: the channel combo on
  `FilteringPanel` (nuclear and cell instances) and on the nuclear
  `MaximaFinderPanel` (`2` → `0` in each), and `MeasurementPanel`'s channel list
  (`[1, 2]` → none) in isolation. Every other control probed kept its edit: the
  text fields on those panels, the per-channel spot panel (its combo is hidden),
  `TopHatFilterPanel`, and `SegmentationPanel` (threshold combo, weighting field and
  the volume/membrane toggles). The full table is in the plan.
- **Why it matters.** The nuclear and cell segmentation channels are chosen on
  the Filtering panels. The control visibly reverts, so it is noticeable, but a
  user who does not re-check carries on with the stale channel. It needs Next then
  Previous with no Preview in between; going forward only is safe because Run
  collects every panel.
- **`MeasurementPanel` is probably protected in the wizard**, because mouse clicks
  on its list and its *Localise Spots* toggle are forwarded to `GIANIUI`, which
  collects the whole frame. That is from reading the code, not a probe, and it
  leaves keyboard-only changes uncovered.
- **The remedy was tested, not assumed.** On both combo panels, `panel.setProperties(
  props, panel)` before `update()` kept the edit. `setVariables()` must not be used
  for these panels: it also calls `setupProcess()`, which builds a preview
  pipeline, and eager collects of unpopulated panels caused the 2026-11-01
  startup crash. Three options are laid out in the plan; the simplest is one call
  in `updateLayer` for the panel being left.
- **A smaller, separate finding.** `TopHatFilterPanel.update()` calls
  `setComponentsEnabled()`, which enables every child. Probe: with top-hat switched
  off the radius field is disabled; after re-entry it is enabled again. The value
  is untouched and the filter still does not run, so it is cosmetic. I noticed it
  while reading, then confirmed it.
- **Not covered:** `SegmentationPanel`'s `setComponentsEnabled()` side effects were
  not probed, and `SelectInputPanel` and `LocalisationPanel` are not `Updateable`,
  so they were out of scope.
- Docs-only; no code change. `AGENTS.md` gains a gotcha recording the audit and
  warning against `setVariables()` as a collect. Version bumped `3.14.17` →
  `3.14.18` (`docs`), kept in sync across `pom.xml`, `CITATION.cff`,
  `docs/conf.py`, and `AGENTS.md`.

## 2026-11-27 — Collect the panel being left on every navigation; top-hat fields stay disabled (3.14.19)

Implements the audit's option A, as Dave chose, and fixes the `TopHatFilterPanel`
display bug the audit also found.

**Collect on leave (all `Updateable` panels).**
- **Fix.** The body of `GIANIUI.updateLayer` is extracted into a static
  `GIANIUI.navigate(all, toShow, props)`. It first calls
  `panel.setProperties(props, panel)` on every panel that is *visible* and
  *`Updateable`*, then hides every panel, then shows `toShow` and calls its
  `update()`. So the channel combos on `FilteringPanel` (nuclear and cell) and the
  nuclear `MaximaFinderPanel`, and `MeasurementPanel`'s list, keep an uncollected
  edit across Next then Previous.
- **Why `setProperties`, not `setVariables()`:** `setVariables()` also calls
  `setupProcess()`, which builds a preview pipeline, and eager collects of
  unpopulated panels caused the 2026-11-01 startup crash.
- **Why visible-only and `Updateable`-only:** `updateLayer` hides every panel on
  every navigation, and a hidden panel's controls may be stale, so writing them
  back could undo a change; and only `Updateable` panels re-read `props`, so the
  rest are collected at Run exactly as before.
- **Why a static method:** `GIANIUI` is a `JFrame` and cannot be built headless;
  extracting the sequence is what lets it be tested at all. The extraction was a
  behaviour-preserving first step, done before the tests.
- **`TrackingPanel` keeps its own `setVisible` collect** (3.14.17). It has unbound
  controls that `setProperties` does not write, so the two collects coexist; the
  second is idempotent.
- **Tests first, seen to fail.** `GIANIUINavigationTest` (8): four structural tests
  on stub panels that pin the *order* (collect, then hide, then update the next
  panel), and four on real panels using the 3-channel fixture (Filtering, nuclear
  Maxima Finder, a parameters load, and Measurement). On the unfixed code the real
  Filtering and Maxima Finder tests failed with `expected: <2> but was: <0>` and the
  two ordering tests with a missing `collect`.
- **Mutation checks.** Dropping the visible-only check failed the hidden-panel test
  (`collect A, collect B` where neither should be collected); dropping the
  `Updateable`-only check failed the non-updateable test (`collect P`); and
  disabling the collect entirely failed five tests including the Measurement one
  (`[1, 2]` → `[]`), which I added after the fix and so checked this way. All were
  reverted; `grep MUTATION src/` is empty.
- **One test has no mutant that fails it:** the parameters-load test passes before
  and after, because `PropertyExtractor`'s own READ refreshes the controls
  regardless of `update()`. It is a sanity guard that collecting on leave does not
  resurrect a stale edit over a load, not a regression detector.

**`TopHatFilterPanel` enabled state.**
- **Fix.** `update()` now re-applies the toggle's enabled state after
  `setComponentsEnabled()` (which enables every child), through a new
  `applyToggleEnabledState()` that the toggle's own handler also uses. The
  condition is `isEnabled() && toggle.isSelected()`, so a panel disabled by
  StarDist stays disabled whatever the toggle says.
- **Tests.** `TopHatFilterPanelEnabledStateTest` (4). Before the fix, re-entry with
  top-hat off failed (`expected: <false> but was: <true>`). Removing the
  `isEnabled() &&` made the StarDist test fail, so that guard is checked.

**Results.** `./mvnw clean test` → **125/125, 0 skipped** (113 + 12). The
fixture-backed tests ran rather than skipping.
- **Not seen in the wizard.** The navigation wiring (that `updateLayer` really calls
  `navigate`) is a one-line delegation not covered by a test of its own; only
  `navigate` itself is.
- **Not covered:** `SegmentationPanel`'s `setComponentsEnabled()` side effects were
  never probed, and `SelectInputPanel` and `LocalisationPanel` are not `Updateable`,
  so they are not collected on leave (they are collected at Run as before).
- Version bumped `3.14.18` → `3.14.19` (`fix`), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-28 — I3 Step 3: shorter panel titles and clearer button text (3.15.0)

- **Panel titles** (`GianiDefaultParams.*_TITLE`; display-only, never stored in
  `properties.xml`, which was checked):

  | Was | Now |
  |---|---|
  | Blob Detection to Approximate Locations of Nuclear Centroids | Find Nuclei |
  | Gaussian Filtering to Suppress Noise in Nuclear Channel Prior to Segmentation | Smooth Nuclear Channel |
  | Top Hat Filtering to Homogenise Background in Nuclear Channel Prior to Segmentation | Remove Background (Nuclear Channel) |
  | Segmentation of Nuclei Using Centroids as Seeds | Segment Nuclei |
  | Gaussian Filtering to Suppress Noise in Cell Channel Prior to Segmentation | Smooth Cell Channel |
  | Segmentation of Cells Using Nuclei as Seeds | Segment Cells |
  | Blob Detection to Approximate Locations of Spots in Channel N | Find Spots in Channel N |

- **Buttons:** the four StarDist/ilastik "Specify Location" buttons now read
  "Browse…", and "Measure Preview" reads "Preview Measurements". All five are
  plain `JButton`s, which `PropertyExtractor` does not harvest, so no stored
  keys change. `docs/measure/specifying_channels.rst` updated to match.
- **Latent bug avoided.** `FilteringPanel`, `SegmentationPanel` and
  `TopHatFilterPanel` decide whether StarDist disables them with
  `this.title.matches(<title constant>)`, which treats the title as a regular
  expression. The old titles contained no regex metacharacters, so it worked by
  accident; "Remove Background (Nuclear Channel)" would have become a regex
  group and never matched, silently leaving the top-hat panel enabled under
  StarDist. All three now use `equals`. Checked by experiment: with `matches`
  restored and the new title, `TopHatFilterPanelEnabledStateTest`'s StarDist case
  fails (`expected: <false> but was: <true>`); with `equals` it passes.
- **Moved to Step 4, not done:** the planned "consistent capitalisation of the
  ilastik labels". Those labels are `MaximaFinderPanel` property keys
  (`setText(propLabels[ILASTIK_*])`), so changing their case is a parameter rename
  needing `DeprecatedProps` entries, not a display fix. The plan had filed it under
  the display-only step by mistake.
- The leftover `DeprecatedProps` entry mapping the even older spot title
  ("Blob Detection to Approximate Locations of Foci in Channel ") now points at
  "Find Spots in Channel ". Titles are not keys, so it is inert; left in place.
- Not checked in the wizard: the new titles' fit in the panel layout (all are
  shorter, so no truncation is expected). The user-doc screenshots still show
  v2.0-era titles (already a TODO).
- `./mvnw clean test` → **125/125, 0 skipped**. No new test: the existing StarDist
  case covers the comparison change, as shown above.
- Version bumped `3.14.19` → `3.15.0` (`feat`: user-visible wording, following
  the precedent of the 3.13.0 tooltip change), kept in sync across `pom.xml`,
  `CITATION.cff`, `docs/conf.py`, and `AGENTS.md`.

## 2026-11-28 — `install-to-fiji.cmd`: finds the wrapper in tool shells; clears stale jars from both folders (3.15.1)

Found while installing 3.15.0 for a live GUI test.

- **The script failed from Claude's shell** with
  `'mvnw.cmd' is not recognized` (the script's own `-q` build only reported
  "build failed"). Cause: that shell sets `NoDefaultCurrentDirectoryInExePath=1`,
  which stops `cmd` running programs from the current directory, and the script
  called the wrapper as a bare `call mvnw.cmd` after `pushd`-ing to the repo
  root. It would have worked from Dave's own terminal. Fix: `call
  "%CD%\mvnw.cmd"`. Verified by running the script with the variable still set:
  exit `0`.
- **It left stale jars behind.** It deleted only `giani-*` from `plugins/` and
  `IAClassLibrary-*` from `jars/`, so the `plugins/IAClassLibrary-v2.5.0.jar`
  from the 2026-11-03 hand install survived next to the new `jars/` copy (Dave
  removed it by hand). It now deletes every `giani-*`, `calm-*` and
  `iaclasslibrary-*` jar (any case) from **both** folders first, reporting each
  one, matching the manual procedure in `AGENTS.md`.
  - My first version of that loop silently removed nothing: a wildcard item in a
    `cmd` `FOR` set (`for %%p in (giani-*.jar ...)`) is expanded against the
    current directory, where nothing matched, so the loop body never ran. The
    prefixes are now listed without wildcards and the pattern is built in the
    inner loop (`"%%~d\%%p-*.jar"`). Second run removed
    `giani-3.15.0.jar` and `iaclasslibrary-1.0.37.jar` from `plugins/` and
    `IAClassLibrary-v2.5.0.jar` from `jars/`, then installed fresh copies.
- **A CALM-era jar had come back.** That run removed
  `plugins/iaclasslibrary-1.0.37.jar`, dated 2026-11-18, from the Fiji where it
  had been removed on 2026-11-03; most likely Fiji's updater reinstalled it from
  the CALM update site. Recorded in `AGENTS.md` (check before a live test) and
  under Phase J3 in `DEVELOPMENT_PLAN.md` (users enabling CALM may get it next to
  `IAClassLibrary-v2.5.0`); the CALM site's file list has not been checked.
- Fiji now holds exactly `plugins/giani-3.15.0.jar` and
  `jars/IAClassLibrary-v2.5.0.jar`. The live GUI test of 3.15.0 has not been run
  yet.
- No Java change; the unit tests were not re-run. Version bumped `3.15.0` →
  `3.15.1` (`fix`), kept in sync across `pom.xml`, `CITATION.cff`,
  `docs/conf.py`, and `AGENTS.md`.
