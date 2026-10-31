# AGENTS.md

Guidance for AI agents working in the GIANI repository.

## Git workflow

This repo's `development` branch is shared, so keep local and remote in sync at
both ends of a session. The 2026-10-22 merge conflict happened because work
committed locally was never pushed at the end of the day, while
`origin/development` advanced independently (H2 output-cleanup vs H3
lineage-tracking).

- **Push at the end of each session** (`git push`) — this is the primary rule;
  unpushed commits are what caused the divergence.
- At the start of a session, `git fetch` then check `git status` and
  `git log @{u}..` to see whether `origin/development` has moved ahead.
- Push before pulling, or rebase onto the latest `origin/development`
  frequently, so divergences stay small and conflicts are rare.

## Project overview

GIANI (General Image Analysis of Nuclei-containing Images) is a [FIJI/ImageJ](https://imagej.net/software/fiji/) plugin for batch analysis of 3D (and 4D) microscopy images: it reads proprietary image formats, segments nuclei and cells, measures morphology/fluorescence intensity, and optionally tracks lineages across time.

- **Language/toolchain:** Java 21, Maven, Swing (NetBeans GUI forms), built as an ImageJ/Fiji plugin.
- **Group/artifact:** `net.calm:giani`, version `3.7.5`.
- **Parent POM:** `org.scijava:pom-scijava:45.1.0` (SciJava build conventions).
- **Core library dependency:** `com.github.djpbarry:IAClassLibrary:v2.4.0` (JitPack) provides the actual image-processing primitives (`MultiThreaded*` process classes, `BioFormatsImg`, `ProcessPipeline`, `PropertyWriter`, etc.). GIANI is mostly glue that wires these primitives into a pipeline and exposes UI/CLI/macro entry points.
- **Transitive dependency:** `mcib3d-core` (via `IAClassLibrary`) provides `mcib3d.geom.Objects3DPopulation`/`Object3D`, the segmentation-result container threaded through the pipeline.
- **Tracking dependency:** `sc.fiji:TrackMate` (declared directly; version managed by `pom-scijava` at 8.0.0) is used only for lineage tracking (`CellLineageTracker`).

## Build / run commands

Tests live under `src/test/java` (JUnit 5, managed by `pom-scijava`). Run them with:

```bash
./mvnw clean test                                      # always `clean` before a full run (see gotcha below)
./mvnw test -Dtest=PipelineExecutorTest                 # single test class
./mvnw test -Dtest=PipelineExecutorTest#methodName      # single test method
./mvnw -Pintegration test                               # include the @Tag("integration") tests
```

CI build (`.github/workflows/maven.yml`, JDK 21):

```bash
./mvnw --batch-mode --update-snapshots verify
```

- The **Maven wrapper** (`./mvnw`, pinned to Maven 3.9.16) is used — no system `mvn` required.
- On Windows, ensure `JAVA_HOME` points to a **JDK 21** before running `./mvnw` (e.g. `JAVA_HOME="<jdk21>" cmd /c mvnw.cmd test`); the project targets Java 21 and `IAClassLibrary` is Java 21 bytecode.
- The POM declares Maven repos `central` (explicit, because `pom-scijava` ≥ 45 drops the implicit Maven Central), `scijava.public`, and `jitpack.io` for its dependencies. It targets **Java 21** via `scijava.jvm.version=21` (required to consume `IAClassLibrary` ≥ 2.0.x, which is compiled at Java 21).
- `mvn_settings.xml` (GitHub Packages) is retained but **not used by CI**: no `pom.xml` dependency resolves from `maven.pkg.github.com`, so it is vestigial for building.
- The `maven-dependency-plugin` is bound to the `package` phase to copy all runtime dependencies into `${project.build.directory}` (so the resulting JAR is runnable alongside its dependencies in a Fiji `plugins/` folder).
- **Always `clean` before a full `test` run.** Surefire executes whatever `.class` files sit in `target/test-classes`, so a test deleted from `src/test/java` keeps running until you clean (a `-Dtest=` filter also sidesteps it).
- **Integration tests silently skip.** `pom.xml` excludes the `integration` group by default; the `integration` profile clears it. Integration tests `Assumptions.assumeTrue(fixture.isFile())` on fixtures under `test_data/`, so a missing/moved fixture skips rather than fails — always check the skip count.

Entry point (configured via `<main-class>` in the POM):

```text
net.calm.giani.exec.GIANI
```

- **No arguments** → launches the Swing GUI (`GIANIUI`).
- **With arguments** → headless batch mode. Flags: `-f <input file>`, `-o <output dir>`, `-l <job list file>`, `-p <properties file>`, `-n <job number>`, `-s <series>`. A `-p` (properties) and either `-f` or `-l` are required. Full usage is documented on the GitHub wiki (`Running-GIANI-on-a-HPC-Cluster`).

## Versioning

The repo follows the sibling-repo convention (IAClassLibrary / ADAPT):

- `pom.xml` `<version>` is a plain `X.Y.Z` with **no `-SNAPSHOT`** suffix.
- Bump `<version>` on **every** code change: `fix`/`refactor`/`chore`/`docs`/`test` → patch, `feat` → minor, breaking change → major.
- Commit messages use [Conventional Commits](https://www.conventionalcommits.org/) (`fix:`, `feat:`, `chore:`, `refactor:`, `docs:`, `test:`; `BREAKING CHANGE:`/`!` for breaking changes).
- Releases are tagged `vX.Y.Z` (e.g. `v3.4.10`).

## Architecture and data flow

The whole analysis is a dataflow pipeline assembled from IAClassLibrary `MultiThreadedProcess` objects.

```
GIANI (CLI) / GIANIUI (Swing wizard) / GIANIMacroExecutor (ImageJ macro)
        |
        v
PipelineBuilder.buildFullPipeline(props, cells)  ->  ProcessPipeline
        |
        v
PipelineExecutor (extends Thread)  ->  iterates files/series, runs each process
```

### Pipeline stages

`PipelineBuilder.buildFullPipeline` (in `src/main/java/net/calm/giani/exec/PipelineBuilder.java`) assembles stages in a **fixed, hard-coded order**, named by the `STAGE_*` constants:

| Index | Constant | Process |
|---|---|---|
| 0 | `STAGE_NUC_MAXIMA` | nuclear centroid/blob detection |
| 1 | `STAGE_NUC_FILTER` | Gaussian filter, nuclear channel |
| 2 | `STAGE_NUC_TOP_HAT` | top-hat background subtraction |
| 3 | `STAGE_NUC_SEGMENT` | watershed, `MultiThreadedWatershed.NUCLEI` |
| 4 | `STAGE_CELL_FILTER` | Gaussian filter, cell channel |
| 5 | `STAGE_CELL_SEGMENT` | watershed, `MultiThreadedWatershed.CELLS` |
| 6 | `STAGE_MEASURE` | `MultiThreadedROIConstructor`, extracts measurements |
| 7+ | — | optional per-channel foci maxima finders, then colocalisation (last stage) |

Each stage's inputs are referenced by **integer index** (`pipeline.getProcess(0)`, `(1)`, `(2)` …). Reordering a stage silently breaks the hard-coded input indices *and* `PipelineBuilderTest`, which pins the order. Change `buildFullPipeline`, the `STAGE_*` constants and the test together.

The cell watershed is built with `remap=true`, so IAClassLibrary remaps cell labels onto the nuclear label space — nucleus, cell and cytoplasm rows therefore share a `Cell_Index` in the results table.

### Execution flow

- `PipelineExecutor.run()` lists input files (via `BioFormatsFileLister`), opens each with `BioFormatsImg` (Bio-Formats), then loops **file → series → timepoint → process**. Each process is submitted to a single-thread executor and joined; parallelism happens *inside* each `MultiThreadedProcess`, not across files or stages. Every process is re-`setup()` per timepoint with `process.setTime(t)`.
- Timepoint range comes from `PipelineExecutor.timeRange(nTime, timeSelect, trackTimelapse)`: a valid `TIME_SELECT` wins; else `TRACK_TIMELAPSE` spans all frames; else just `t = 0`.
- Results are written to `<output-dir>/GIANI_Output.csv` (`DataWriter.saveResultsTable`) and parameters to `<output-dir>/properties.xml` (`PropertyWriter.saveProperties`).

## Code organization

```
src/main/java/net/calm/giani/
  exec/            CLI entry (GIANI), pipeline assembly (PipelineBuilder), runner (PipelineExecutor)
  ui/              Swing wizard panels (one per pipeline stage), extends LayerPanel
  gianiparams/     parameter definitions, tooltips, deprecated-name migration
  macro/           ImageJ Macro Language extension (MacroExtension)
src/main/resources/icon/icon.png      app icon
src/main/resources/plugins.config     Fiji plugin config (registers the GUI + macro extension)
```

### Plugin discovery in Fiji

- `src/main/resources/plugins.config` registers the GUI under `Plugins>CALM`
  (`exec.GIANI`, which implements `ij.plugin.PlugIn`) and the macro extension
  (`GIANIMacroExecutor`), exposing `loadPropertiesFile` / `setProperty` /
  `getPropertyList` / `run` to ImageJ macros as `Ext.GIANI.<function>`.
- The Swing GUI (`GIANIUI`) is launched from `GIANI.main()` (run the JAR with no
  arguments).

## Conventions and patterns

### Parameters are `Properties` keyed by human-readable strings

There is no typed config object. All parameters live in a `java.util.Properties` instance. The keys are user-facing English strings defined as `public static final String` constants in `GianiDefaultParams` (which extends `Properties`). Examples:

- `GianiDefaultParams.INPUT_DIR_LABEL` → `"Input Directory"`
- `GianiDefaultParams.BLOB_NUC_CHAN_SELECT_LABEL` → `"Nuclear Channel"`
- `GianiDefaultParams.CHAN_FOR_MEASURE` → `"Select Channels to Measure"`

Values are always strings (`"true"`, `"0.0"`, `"0"`, etc.), parsed on use via `Integer.parseInt` / `Boolean.parseBoolean` / `Double.parseDouble`.

### Mapping parameters to process classes via `propLabels`

Each `MultiThreadedProcess` subclass defines integer constants (e.g. `MultiThreadedMaximaFinder.CHANNEL_SELECT`, `.BLOB_DETECT`, `.SERIES_SELECT`) and a length `N_PROP_LABELS`. Pipeline factory methods build a `String[] propLabels` of that length, mapping each process index to a `GianiDefaultParams` constant, then call `process.setup(new BioFormatsImg(), props, propLabels)`. This array is also returned by `process.getPropLabels()` and used by the UI panels to bind fields. When adding a new parameter, update `GianiDefaultParams`, the corresponding `propLabels` mapping in `PipelineBuilder`, and the matching UI panel.

### Per-channel parameters use `%s%d` suffixes

Spot/foci parameters are per-channel: the label constant ends in `"Channel "` and a numeric suffix is appended at runtime, e.g. `String.format("%s%d", GianiDefaultParams.BLOB_CHAN_RAD_LABEL, i)`. Channel selection is stored as an integer bitmask string in `CHAN_FOR_MEASURE`; `buildFullPipeline` iterates bits `0..MAX_CHANNELS-1` (max 10 channels). Keep `MAX_CHANNELS` in sync with `GIANIParamInfos.MAX_CHAN`.

### Swing UI: NetBeans forms, do not hand-edit generated code

Each `.java` UI file has a paired `.form` (NetBeans Matisse) file. The `initComponents()` method is delimited by:

```java
// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
...
}// </editor-fold>//GEN-END:initComponents
```

and the field declarations are delimited by `//GEN-BEGIN:variables` … `//GEN-END:variables`. These are regenerated by the NetBeans Form Editor; edit them only through the form, not by hand. Custom logic goes in methods with `//GEN-FIRST:event_...` markers.

Panels extend `net.calm.iaclasslibrary.UIClasses.LayerPanel` and implement `Updateable` where they need to refresh on wizard navigation. `GIANIUI` is a `LinkedList<LayerPanel>` wizard; `nextButton`/`previousButton` advance/retreat `layerIndex` and toggle panel visibility (there is no incremental `addProcess()`/`removeProcess()` — the pipeline is assembled once from config). Each panel holds a `Supplier<MultiThreadedProcess>` factory that calls `GIANIUI.buildPreviewProcess(stage)` → `collectPanelState()` + `PipelineBuilder.buildPreviewProcess(...)`, so previews build a fresh pipeline per stage rather than reusing a long-lived process. `TrackingPanel` is hand-written (no `.form`) and is inserted at index 1 after `initComponents()`.

### Deprecated parameter migration

`GianiDefaultParams.updateDeprecatedProps` rewrites old parameter names to current ones using `DeprecatedProps.getDepMap()`, matched by `String.startsWith` (so per-channel suffixed names still match). `GIANIUI.loadParameters` and macro/headless property loading call this after loading. When renaming a parameter, add the old→new mapping here rather than silently breaking old saved property files.

## Gotchas and non-obvious details

- **License:** GPL-3.0-or-later — `LICENSE`, all source headers, and `pom.xml` (`license.licenseName = gpl_v3`) now agree.
- **Default paths:** `GianiDefaultParams.initialise()` uses `System.getProperty("user.home")` for the input dir (no more hard-coded Dropbox/ilastik paths); expect to override via a properties file for real runs.
- **`GianiDefaultParams.TITLE` is derived from the JAR filename.** `getTitleWithVersion()` reads the code source location and requires the filename to start with `GIANI` (case-insensitive), otherwise falls back to `"GIANI"`. Output directory naming uses this title (`<parent>/<TITLE>_<label>`).
- **Headless mode sets `java.awt.headless=true`** and logs via `ij.IJ.log` (not stdout). It calls `System.exit(0)` on completion and on several early-exit error paths.
- **`DebugTools.setRootLevel("WARN")`** is called in both `GIANI.run()` and `PipelineExecutor.run()` to quiet Bio-Formats/loci logging. Keep this when adding new entry paths, or console output becomes extremely noisy.
- **Series with fewer than 2 Z-slices are skipped** (`img.getSizeZ(s) < 2`) — 2D images are silently ignored.
- **`.gitattributes` marks `*.zip` as Git LFS.** `assets/Test_Data.zip` is a ~335 MB LFS file; do not treat it as a normal tracked blob.
- **`TRACK_TIMELAPSE` ("Track Timelapse") does not track anything** — it only widens the time loop so every timepoint is analysed. `TRACK_LINEAGE` ("Track Lineage") is what runs TrackMate and produces the `Track ID` column, `GIANI_tracks.csv` and the `*_track_masks/` folders. The two flags are independent; at defaults (`TRACK_LINEAGE=true`, `TRACK_TIMELAPSE=false`) only `t=0` runs, so no tracking output is produced.
- **No logging framework:** errors go through `GenUtils.logError(Throwable, String)` (IAClassLibrary utility), which typically routes to ImageJ's log.

## Documentation conventions

- **Keep `DEVELOPMENT_PLAN.md` and `REVISION_LOG.md` in sync with every repo change, in the same pass.** `REVISION_LOG.md` is the source of truth for what was actually done (dated entries); `DEVELOPMENT_PLAN.md` holds the phase plan. In practice both drift — **treat plan/log status markers as unverified and check against source** before acting on them.
- **Bump `<version>` on every code change** in three files that must stay in sync: `pom.xml`, `docs/conf.py`, and `CITATION.cff` (`fix`/`refactor`/`chore`/`docs`/`test` → patch, `feat` → minor, breaking → major).

## What to verify after changes

- `./mvnw clean test` succeeds; if you touched the integration path, run `./mvnw -Pintegration test` and check the skip count.
- If a parameter label changed, grep for its constant usage across `PipelineBuilder`, `GIANIUI`, and the relevant panel, and consider adding a `DeprecatedProps` entry.
- If a pipeline stage was added/reordered/removed, update the `STAGE_*` constants, `PipelineBuilder.buildFullPipeline`, `GIANIUI.initComponents`, and `PipelineBuilderTest` together.
- Bump the version in `pom.xml`, `docs/conf.py`, and `CITATION.cff` (see Versioning).
