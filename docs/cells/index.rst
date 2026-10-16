************
Segmenting Cells
************

The approach to cell segmentation is very similar to that used for segmenting nuclei, the only difference being that the nuclei segmented in the previous step are now used as seeds for segmenting cells.

.. note:: It is not explicitly necessary to have a separate channel containing a specific marker for cell volumes or boundaries. For example, the nuclear channel could be reused as the cell channel, in which case the resulting segmentations corresponding to nuclei and cells will be equivalent. GIANI is somewhat agnostic about the concept of a "cell" - what constitutes a cell is somewhat up to you!

.. toctree::
  :maxdepth: 2
  
  filter_cells
  segment_cells