*******************************
Timelapse and Lineage Tracking
*******************************

GIANI can analyse timelapse (3D + time) data. Each timepoint is segmented and
measured as an independent 3D stack, and the segmented cells can then be
linked across timepoints to follow cell lineages, including cell divisions.
Tracking uses `TrackMate <https://imagej.net/plugins/trackmate/>`__'s LAP
tracker internally - you do not need to run TrackMate yourself.

These settings are on the *Tracking* panel, which appears straight after
input selection when *Show Advanced Options* is enabled (see
:doc:`index`).

.. TODO(screenshot): add a screenshot of the Tracking panel (suggested name
   images/GIANI_Tracking_Panel.png).

.. important:: By default, GIANI analyses only the **first** timepoint of each
   series, so no tracking output is produced. To track cells, set *Timepoints
   to Analyse* to **All timepoints** and switch on *Track Lineage*.

Parameters
==========

-  **Timepoints to Analyse:** choose *First timepoint only* (the default),
   *All timepoints*, or *A single timepoint*. This controls which timepoints
   are analysed; it does not track anything by itself.
-  **Timepoint:** which single timepoint to analyse, counting from **1**.
   Only available when *A single timepoint* is selected.
-  **Track Lineage:** link the segmented cells across timepoints into
   lineages. Each cell is represented by the centre of its nucleus. Because
   tracking needs more than one timepoint, this is **only available when
   analysing all timepoints** - selecting any other mode switches it off.
-  **Linking Distance:** *Automatic* (the default) sets the maximum distance a
   cell may move between consecutive timepoints to three times the median
   nuclear radius, which suits most data. Choose *Specify below* to set it
   yourself.
-  **Max Linking Distance:** the distance used when *Linking Distance* is set
   to *Specify below*, in calibrated units (e.g. microns). The same value
   limits gap-closing and division detection. Too small and tracks break; too
   large and nearby cells get swapped.
-  **Max Missing Timepoints:** the maximum number of consecutive timepoints a
   cell may be missing (e.g. due to a failed segmentation) and still be linked
   back into the same track. The default is ``2``.

.. note:: These controls are a friendlier view of the parameters stored in
   ``properties.xml``, which are unchanged: *Timepoints to Analyse* and
   *Timepoint* map onto ``Process All Timepoints`` and ``Specific Timepoint``
   (which remains 0-based, with ``-1`` meaning unset), and *Linking Distance* /
   *Max Linking Distance* map onto ``Linking Distance`` (``-1`` meaning
   automatic). Existing parameter files, the :doc:`command line
   <../cluster/index>` and ``Ext.GIANI.setProperty`` are unaffected.
   ``Max Frame Gap`` was renamed to ``Max Missing Timepoints``; old files are
   migrated automatically.

.. _tracking-output:

Tracking output
===============

When tracking is enabled, the following are added to the
:doc:`output folder <../complete/index>`:

* A ``Track ID`` column in the main results CSV and in
  ``spot_summary_data.csv``, giving the lineage each cell belongs to. ``0``
  means the cell was not assigned to a track.
* ``GIANI_tracks.csv``, which lists every tracked cell at every timepoint
  (``track_id, t, z, y, x, parent_track_id, radius``). When a cell divides,
  each daughter starts a new track whose ``parent_track_id`` is the mother's
  track; ``-1`` marks a track with no parent.
* ``*_track_masks`` folders alongside the ordinary mask folders, in which each
  cell is labelled with its track ID instead of its per-timepoint index, so a
  tracked cell keeps the same label over time. ``0`` is background and ``1``
  is a cell that was not tracked. Open these in FIJI with a label lookup table
  (e.g. *glasbey*) to inspect the tracking visually.

Track IDs are the same in the CSV files and the track masks.

Viewing tracks in inTRACKtive
=============================

``GIANI_tracks.csv`` uses the
`Ultrack <https://github.com/royerlab/ultrack>`__ CSV format, so it can be
viewed directly in the browser-based
`inTRACKtive <https://github.com/royerlab/inTRACKtive>`__ viewer. With
inTRACKtive installed (a Python package), run::

   intracktive open GIANI_tracks.csv

See the inTRACKtive documentation for options such as sizing cells by the
``radius`` column.
