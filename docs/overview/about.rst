**************
What is GIANI?
**************

GIANI is a FIJI plugin for the routine analysis of 3D (and 3D + time) cell
biology image data. Implemented specifically with batch-processing in mind,
GIANI is designed to:

* Read data sets in any format recognised by Bio-Formats
* Identify the centres of nuclei, using one of several detection methods
* Segment nuclei fully, using either mcib3d or MorphoLibJ
* Segment cells, using the nuclei as seeds
* Measure nuclear and cell morphology, together with fluorescence intensities
  within these regions
* Optionally detect sub-cellular spots and relate them to the cells that
  contain them
* Optionally analyse every timepoint of a timelapse and track cell lineages
  over time (see :doc:`../start/tracking`)

GIANI is deliberately focused on this one guided workflow. If you need custom
analysis pipelines, `MIA <https://github.com/mianalysis/mia>`__ is a more
general-purpose alternative.

Click `here <https://doi.org/10.1242/jcs.259511>`__ to read the paper
introducing GIANI (Journal of Cell Science, 2022).
