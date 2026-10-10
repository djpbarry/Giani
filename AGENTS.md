# AGENTS.md

Guidance for AI agents working in the GIANI repository.

## Project overview

GIANI (General Image Analysis of Nuclei-containing Images) is a [FIJI/ImageJ](https://imagej.net/software/fiji/) plugin for batch analysis of 3D microscopy images: it reads proprietary image formats, segments nuclei and cells, measures morphology/fluorescence intensity, and lets users browse results.

- **Language/toolchain:** Java 11, Maven, Swing (NetBeans GUI forms), built as an ImageJ/Fiji plugin.
- **Group/artifact:** `net.calm:giani`, version `3.4.9`.
- **Parent POM:** `org.scijava:pom-scijava:40.0.0` (SciJava build conventions).
- **Core library dependency:** `com.github.djpbarry:IAClassLibrary:fe92f24c6e` (pulled from JitPack) provides the actual image-processing primitives (`MultiThreaded*` process classes, `BioFormatsImg`, `ProcessPipeline`, `PropertyWriter`, etc.). GIANI is mostly glue that wires these primitives into a pipeline and exposes UI/CLI/macro entry points.
- **Other dependency:** `com.github.mcib3d:mcib3d-plugins:3.96.3` (with `imageware` and `mcib3d-core` excluded) provides 3D geometry (`mcib3d.geom.Object3D`, `Objects3DPopulation`).

## Build / run commands

There are **no unit tests** in this repository (`src/test` does not exist). Verification means "does it compile and package".

CI build (`.github/workflows/maven.yml`, JDK 11):

```bash
mvn --batch-mode --update-snapshots -Dinternal.repo.password="$PAT" --settings mvn_settings.xml verify
```

- `mvn_settings.xml` activates a `github` profile that adds the GitHub Packages repo `https://maven.pkg.github.com/djpbarry/*` and authenticates with username `djpbarry` / password `${internal.repo.password}`. The `PAT` is a GitHub Actions secret; locally you must supply your own token via `-Dinternal.repo.password=...` if that repo is needed.
- The POM also declares Maven repos `scijava.public` (`https://maven.scijava.org/content/groups/public`) and `jitpack.io` for its dependencies.
- The `maven-dependency-plugin` is bound to the `package` phase to copy all runtime dependencies into `${project.build.directory}` (so the resulting JAR is runnable alongside its dependencies in a Fiji `plugins/` folder).

Entry point (configured via `<main-class>` in the POM):

```text
net.calm.giani.exec.GIANI
```

- **No arguments** → launches the Swing GUI (`GIANIUI`).
- **With arguments** → headless batch mode. Flags: `-f <input file>`, `-o <output dir>`, `-l <job list file>`, `-p <properties file>`, `-n <job number>`, `-s <series>`. A `-p` (properties) and either `-f` or `-l` are required. Full usage is documented on the GitHub wiki (`Running-GIANI-on-a-HPC-Cluster`).

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

`PipelineBuilder.buildFullPipeline` (in `src/main/java/net/calm/giani/exec/PipelineBuilder.java`) assembles stages in a **fixed, hard-coded order**:

1. `getDefaultMaximaFinder` — nuclear centroid/blob detection
2. `getDefaultNucFilteringProcess` — Gaussian filter (nuclear channel)
3. `getDefaultNucTopHatFilteringProcess` — top-hat background subtraction
4. `getDefaultNucSegmenter` — watershed, `MultiThreadedWatershed.NUCLEI`
5. `getDefaultCellFilteringProcess` — Gaussian filter (cell channel)
6. `getDefaultCellSegmenter` — watershed, `MultiThreadedWatershed.CELLS`
7. `getDefaultMeasure` — `MultiThreadedROIConstructor`, extracts measurements
8. Optional: one `getDefaultFociMaximaFinder` per selected channel, then `getDefaultColocalisationProcess`

Each stage's inputs are referenced by **integer index** (`pipeline.getProcess(0)`, `(1)`, `(2)` …). This order is coupled across three places: `PipelineBuilder.buildFullPipeline`, `GIANIUI.initComponents`, and the wizard panel wiring. **Do not reorder stages without updating all three.**

### Execution flow

- `PipelineExecutor.run()` lists input files (via `BioFormatsFileLister`), opens each with `BioFormatsImg` (Bio-Formats), then for each series ≥ 2 Z-slices re-runs every process in the pipeline. Execution of a single process is submitted to a single-thread executor; parallelism happens *inside* each `MultiThreadedProcess`, not across files.
- Results are written to `<output-dir>/GIANI_Output.csv` (`DataWriter.saveResultsTable`) and parameters to `<output-dir>/GIANI.xml` (`PropertyWriter.saveProperties`).

## Code organization

```
src/main/java/net/calm/giani/
  exec/            CLI entry (GIANI), pipeline assembly (PipelineBuilder), runner (PipelineExecutor)
  ui/              Swing wizard panels (one per pipeline stage), extends LayerPanel
  gianiparams/     parameter definitions, tooltips, deprecated-name migration
  ResultsBrowser/  post-analysis results browser (Fiji TrackMate + mcib3d)
  macro/           ImageJ Macro Language extension (MacroExtension)
src/main/resources/icon/icon.png   app icon
```

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

Panels extend `net.calm.iaclasslibrary.UIClasses.LayerPanel` and implement `Updateable` where they need to refresh on wizard navigation. `GIANIUI` is a `LinkedList<LayerPanel>` wizard; `nextButton`/`previousButton` advance/retreat `layerIndex`, calling `addProcess()`/`removeProcess()` to add/remove the panel's process from the running `ProcessPipeline`.

### Deprecated parameter migration

`GianiDefaultParams.updateDeprecatedProps` rewrites old parameter names to current ones using `DeprecatedProps.getDepMap()`, matched by `String.startsWith` (so per-channel suffixed names still match). `GIANIUI.loadParameters` and macro/headless property loading call this after loading. When renaming a parameter, add the old→new mapping here rather than silently breaking old saved property files.

## Gotchas and non-obvious details

- **License mismatch:** the `LICENSE` file is GPL-3.0 and all source headers declare GPL-3.0, but `pom.xml` declares `Simplified BSD License` (`license.licenseName = bsd_2`). Treat the actual license as GPL-3.0 (the LICENSE file and headers agree with each other); the POM metadata is inconsistent.
- **Hard-coded developer defaults:** `GianiDefaultParams.initialise()` sets `INPUT_DIR_LABEL` to a Dropbox path (`E:\Dropbox (The Francis Crick)\...`) and `ILASTIK_PROJECT_FILE`/`ILASTIK_DIRECTORY` to absolute Windows paths. These are debugging leftovers; do not rely on them and expect to override via a properties file.
- **`GianiDefaultParams.TITLE` is derived from the JAR filename.** `getTitleWithVersion()` reads the code source location and requires the filename to start with `GIANI` (case-insensitive), otherwise falls back to `"GIANI"`. Output directory naming uses this title (`<parent>/<TITLE>_<label>`).
- **Headless mode sets `java.awt.headless=true`** and logs via `ij.IJ.log` (not stdout). It calls `System.exit(0)` on completion and on several early-exit error paths.
- **`DebugTools.setRootLevel("WARN")`** is called in both `GIANI.run()` and `PipelineExecutor.run()` to quiet Bio-Formats/loci logging. Keep this when adding new entry paths, or console output becomes extremely noisy.
- **Series with fewer than 2 Z-slices are skipped** (`img.getSizeZ(s) < 2`) — 2D images are silently ignored.
- **`.gitattributes` marks `*.zip` as Git LFS.** `assets/Test_Data.zip` is a ~335 MB LFS file; do not treat it as a normal tracked blob.
- **`SelectInputPanel` and other panels keep `static` state** (e.g. `static File inputDirectory`), so there is shared state across panel instances.
- **No logging framework:** errors go through `GenUtils.logError(Throwable, String)` (IAClassLibrary utility), which typically routes to ImageJ's log.

## What to verify after changes

- `mvn verify` succeeds (with appropriate settings/token).
- If a parameter label changed, grep for its constant usage across `PipelineBuilder`, `GIANIUI`, and the relevant panel, and consider adding a `DeprecatedProps` entry.
- If a pipeline stage was added/reordered/removed, update the integer indices in both `PipelineBuilder.buildFullPipeline` and `GIANIUI.initComponents`.
