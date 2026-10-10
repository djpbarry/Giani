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
