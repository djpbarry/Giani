Filtering Prior to Cell Segmentation
************************************

.. figure:: ../images/GIANI_Filter_Cells.PNG
   :alt: Filtering for cell segmentation

Prior to cell segmentation, as previously explained for nuclei, it's a good idea to reduce noise in the input image. The channel specified by *Channel for Cell Segmentation* will be Gaussian filtered here, using the specified *Filter Radius for Cell Channel* (the standard deviation of the Gaussian, in calibrated units; default ``1.0``). The output of this filtering operation will be used in the next step, the aim of which is complete segmentation of the cells.

When you are happy with the level of filtering applied, click next to proceed to the next step, :doc:`cell segmentation <segment_cells>`.
