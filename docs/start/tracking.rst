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

.. important:: By default, GIANI only analyses the **first** timepoint of each
   series, so no tracking output is produced. To track cells, you must enable
   both *Process All Timepoints* and *Track Lineage*.

Parameters
==========

-  **Process All Timepoints:** analyse every timepoint of each series. When
   off (the default), only the first timepoint is analysed. This setting does
   not track anything by itself - it only controls which timepoints are
   analysed.
-  **Specific Timepoint:** analyse only this timepoint (numbered from 0).
   A value of ``-1`` (the default) disables this option. When set to a valid
   timepoint, it overrides *Process All Timepoints*.
-  **Track Lineage:** link the segmented cells across timepoints into
   lineages. Each cell is represented by the centre of its nucleus. Has no
   effect unless more than one timepoint is analysed.
-  **Linking Distance:** the maximum distance (in calibrated units, e.g.
   microns) a cell may move between consecutive timepoints and still be
   linked. The same value limits gap-closing and division detection. A value
   of ``-1`` (the default) sets it automatically to three times the median
   nuclear radius.
-  **Max Frame Gap:** the maximum number of consecutive timepoints a cell may
   be missing (e.g. due to a failed segmentation) and still be linked back
   into the same track. The default is ``2``.

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
