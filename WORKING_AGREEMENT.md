# Working agreement: what Claude may do without asking

This file records how Claude Code and Dave work together in this repo. It sets out what Claude may do on its own, what needs a "yes" first, and what Claude should never do.

The hard limits are also enforced by `.claude/settings.json` (permission `allow` / `ask` / `deny` rules), which Claude Code applies whatever the model decides. This file covers the judgment calls that can't be written as command patterns. **Update both as we go**: when Dave says "you can just do X from now on" or "never do Y", add it here (and to `settings.json` if it's a command).

Sections 1–4 are generic and meant to be copied to other repos (or to `~/.claude/CLAUDE.md` for all repos). Section 5 is specific to this project. When copying, replace section 5.

## 1. How we work

- **Planning is not permission.** When we're discussing or planning a feature, or Dave has answered design questions, summarise the plan and wait for an explicit "go ahead", "make the changes" or "implement it" before editing files. (Learned 2026-11-06: a feature was implemented during what was meant to be planning.)
- **Claude prepares changes; Dave commits and pushes.** Claude edits files, checks them as far as it can, and reports what changed. Dave reviews, commits and pushes, unless he explicitly asks Claude to.
- **After an interruption or a rejected tool call, stop.** Don't retry or carry on with the next step; say where things stand and wait. A rejection may be deliberate even if the terminal looked frozen.
- **Report results faithfully.** Say what was actually run and what wasn't (e.g. "compiles, but not run end to end"). Don't call something tested unless it was run.
- **Decisions that change results** (defaults, units, parameters, algorithm choices) are Dave's: lay out the options with a recommendation and ask.
- **Keep the docs in step.** When behaviour, flags or plans change, update the project's agent notes (`CLAUDE.md`, or `AGENTS.md` where a repo has one) and `docs/` / `README.md` if users see it, in the same change.

## 2. OK without asking

- **Reading**: any file in the repo; searching (Glob / Grep); `git status`, `log`, `diff`, `show`, `branch` (listing), `fetch`, `check-ignore`, `ls-files`, `rev-parse`.
- **Editing code and docs in the repo** once Dave has asked for the change (see "Planning is not permission"). No new edits after the requested change is done without checking.
- **Source files in the repo: OK to create or edit without asking** while working on something Dave has asked for. This covers the code itself; "Planning is not permission" still applies to *starting* new work. Which files count is listed in §5.
- **Markdown files (`.md`) in the repo: OK to create or edit without asking** (added 2026-11-21), e.g. `CLAUDE.md`, `AGENTS.md`, `README.md`, `docs/*.md`: recording findings, test results, plans, notes and doc updates. Still say what changed. **Exception: this file (`WORKING_AGREEMENT.md`)**: changing its rules needs a "yes" first (see §3), except recording a rule Dave has just stated.
- **Scratch work**: writing and running throwaway scripts, test data and outputs in the session's scratchpad directory (never in the repo).
- **Checks**: compiling and running the project's unit tests in the existing environment without installing anything new (the exact commands are in §5).
- **Web lookups**: reading documentation and package pages (package registries, GitHub, project docs) to check APIs.
- **Memory**: saving or updating Claude's memory notes about how we work.

## 3. Ask first

- **Git that changes state**: `add`, `commit`, `push`, `pull`, `switch` / `checkout`, `merge`, `rebase`, `stash`, creating a branch, tagging.
  - Before switching branches, check `git status` and stop if there are uncommitted changes or untracked files that would be overwritten or carried along.
- **Environments and dependencies**: adding, removing or re-versioning a dependency, changing the build tooling, or anything else that downloads and installs packages (project specifics in §5).
- **Moving, renaming or deleting files in the repo**, including untracked ones (e.g. a stray `pixi.lock`). Back up to the scratchpad rather than deleting, and say so.
- **Long or heavy runs**: the project's main pipeline or entry point, or anything expected to take more than a few minutes, or to use a lot of memory or disk.
- **Anything outward-facing**: publishing (Artifacts, docs, gists), posting to GitHub (issues, PRs, comments), sending anything to an external service, or contacting servers on Dave's behalf (e.g. data servers or remote APIs).
- **Changing Claude Code settings**: `.claude/settings*.json`, hooks, or this file's rules (except recording a rule Dave has just stated).

## 4. Never (unless Dave explicitly asks for that specific action)

- `git push --force` / `-f`, `git reset --hard`, `git clean`, `git branch -d` / `-D`, rewriting published history (`rebase` / `commit --amend` on pushed commits).
- Recursive or bulk deletes (`rm -rf`, `Remove-Item -Recurse`), or deleting anything outside the scratchpad.
- Hand-editing generated files or binary test data (project specifics in §5).
- Disabling safety checks: `--no-verify`, skipping hooks, turning off TLS verification to get round certificate errors.
- Putting credentials on a command line or in files; use an existing login session or credential store.
- Changing another user's state on a shared service (e.g. their session, group or permissions).

## 5. This project

GIANI is a Java 21 / Maven Fiji plugin; build, architecture and conventions are in `AGENTS.md`.

- **Local machine**: Windows. `JAVA_HOME` must point to a JDK 21 and is normally already set; the Fiji location is in the gitignored `bin/local-env.cmd`. Builds use the Maven wrapper (`./mvnw` / `.\mvnw.cmd`); no system Maven.
- **OK without asking**:
  - Editing Java under `src/main/java` and `src/test/java` (agreed 2026-11-26).
  - Editing `pom.xml`, including the version bump on every change that `AGENTS.md` requires, along with `CITATION.cff`, `docs/conf.py` and the `docs/**/*.rst` user docs. Dependency changes in `pom.xml` still need a "yes" (§3).
  - Running the unit tests: `./mvnw clean test`, or one class or method with `-Dtest=…` (about 2 min for the full suite, agreed 2026-11-26).
  - `bin\smoke-test-fiji.cmd` (a read-only classpath check).
- **Ask first**:
  - `./mvnw -Pintegration …`: the end-to-end pipeline, about 4–6 min locally.
  - `bin\install-to-fiji.cmd`, which writes into the Fiji install outside the repo, and `bin\run-fiji.cmd`, which launches the GUI.
  - Changing `.mvn/` (wrapper pin), `bin/local-env.cmd` or `JAVA_HOME`.
  - Dependency changes in `pom.xml` (IAClassLibrary, TrackMate, parent POM, repositories, plugins).
- **Never hand-edit**: `assets/Test_Data.zip` (Git LFS) or the `test_data/` fixtures that the integration tests depend on.
- **Commit and push**: `development` is shared, and `AGENTS.md` asks that each session's work be pushed. Claude prepares the changes and, at the end of a session, reminds Dave of anything uncommitted or unpushed (or asks to commit and push it).
- **Output locations**: temporary files go in the scratchpad, not the repo.
