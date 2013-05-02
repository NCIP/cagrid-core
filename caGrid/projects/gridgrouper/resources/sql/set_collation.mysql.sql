/*
============================================================================
  Copyright The Ohio State University Research Foundation, The University of Chicago - 
	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
	Ekagra Software Technologies Ltd.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
============================================================================
*/
ALTER TABLE `grouper_members` CHANGE `subject_id` `subject_id` VARCHAR( 255 ) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL 