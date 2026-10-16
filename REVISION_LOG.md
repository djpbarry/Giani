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
