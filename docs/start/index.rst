***************
Getting Started
***************

To run GIANI, ensure you have :doc:`installed it <../overview/installation>`.
Then, you should find GIANI directly on FIJI’s *Plugins* menu:

.. figure:: images/GIANI_Plugins_Menu.png
   :alt: GIANI on FIJI’s Plugins menu

   GIANI on FIJI’s Plugins menu

.. TODO(screenshot): recapture GIANI_Plugins_Menu.png - GIANI is now directly
   on the Plugins menu (no CALM submenu, no Results Browser).

GIANI is a step-by-step wizard. Use *Next* and *Previous* to move between
steps, and *Run* to analyse all of the images in your input directory.

Quick start: the default path
=============================

By default, the wizard only shows the essential steps:

#. :doc:`Select your input directory <input>` (and, optionally, a
   :doc:`preview data set <preview>`).
#. :doc:`Choose which channels to measure <../measure/specifying_channels>`.
#. Click *Run*.

Every other parameter uses a sensible default value. When the analysis
finishes, open the :doc:`output folder <../complete/index>` to find the
results.

Advanced Options
================

To fine-tune the analysis, click *Show Advanced Options* in the wizard's
button bar. This reveals every step of the pipeline:

* :doc:`tracking` (timelapse and lineage tracking)
* :doc:`../nuclei/index` (centre detection, filtering, background
  subtraction and segmentation)
* :doc:`../cells/index` (filtering and segmentation)
* Per-channel spot detection settings, when :doc:`spot localisation
  <../measure/localising_spots>` is enabled

Each step has a *Preview* button so you can check the effect of your
parameters on the preview data set before running the full analysis. Click
*Hide Advanced Options* to return to the default path; your settings are
kept.

.. TODO(screenshot): add a screenshot of the button bar showing the
   Show/Hide Advanced Options button (suggested name GIANI_Advanced_Options.png).

Saving and loading parameters
=============================

Every analysis saves its parameters to a ``properties.xml`` file in the
output folder. To reuse them, click *Load Parameters* in the wizard and
select that file - all panels are then populated with the saved values. The
same file is used to :doc:`run GIANI headlessly <../cluster/index>`.

**Jump to...**

.. toctree::
  :maxdepth: 2

  input
  preview
  tracking
