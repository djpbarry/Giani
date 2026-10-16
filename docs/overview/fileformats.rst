General
-------

GIANI uses `Bio-Formats <https://www.openmicroscopy.org/bio-formats/>`__
to read image data, so in theory, anything that can be read by
Bio-Formats is suitable for analysis by GIANI. The input directory
supplied to GIANI can contain a mixture of different file types,
including multi-file image formats (a single image saved in multiple
files) and/or multi-series formats (multiple images contained within a
single file) - as long as the images are readable by Bio-Formats, they
should be readable by GIANI.

However, it should be remembered that different file formats likely
indicates data that was acquired on different systems and, therefore,
exhibits different properties. It may therefore be challenging to
specify a single set of analysis parameters for GIANI to successfully
analyse such heterogeneous data. It is therefore recommended that input
directories should consist of image data representing a single
experiment, or, perhaps, a series of similar experiments, acquired on
the same system.

Data Dimensionality
-------------------

At present, one of GIANI’s dependencies requires 3D input. Therefore, at
this time, GIANI can only be run on 3D datasets - any 2D datasets that
GIANI encounters will be skipped, accompanied by the following prompt:

``No valid image series found in <filename>``

Converting Data
---------------

If for some reason GIANI cannot read your data (correctly), it is
suggested that you use Bio-Formats to convert your data to OME.TIFF
format. This can be done using either the Bio-Formats plugin for
ImageJ/FIJI, or using the Bio-Formats command line tools. Instructions
on how to use Bio-Formats are available
`here <https://docs.openmicroscopy.org/bio-formats/6.9.0/users/index.html>`__.
