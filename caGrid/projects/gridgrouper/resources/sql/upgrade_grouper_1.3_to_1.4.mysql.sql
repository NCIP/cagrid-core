/*
============================================================================
  Copyright The Ohio State University Research Foundation, The University of Chicago - 
	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
	Ekagra Software Technologies Ltd.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
============================================================================
*/
create table gridgrouper_membershiprequest (
	id varchar(128) not null,
	requestor varchar(255) not null,
	group_id varchar(128) not null,
	request_time bigint,
	status varchar(255) not null,
	reviewer_id varchar(128),
	review_time bigint,
	public_note varchar(255),
	admin_note varchar(255),
	primary key (id)
);

create table gridgrouper_membershiprequest_history (
	id varchar(128) not null,
	membershiprequest_id varchar(128) not null,
	status varchar(255) not null,
	reviewer_id varchar(128),
	update_date bigint,
	public_note varchar(255),
	admin_note varchar(255),
	primary key (id)
);


alter table gridgrouper_membershiprequest_history add index FKF6C38EB551626995 (reviewer_id), add constraint FKF6C38EB551626995 foreign key (reviewer_id) references grouper_members (id);
alter table gridgrouper_membershiprequest_history add index FKF6C38EB53847521 (membershiprequest_id), add constraint FKF6C38EB53847521 foreign key (membershiprequest_id) references gridgrouper_membershiprequest (id);

alter table gridgrouper_membershiprequest add index FKCF5D636051626995 (reviewer_id), add constraint FKCF5D636051626995 foreign key (reviewer_id) references grouper_members (id);
alter table gridgrouper_membershiprequest add index FKCF5D63601E2E76DB (group_id), add constraint FKCF5D63601E2E76DB foreign key (group_id) references grouper_groups (owner_id);

alter table gridgrouper_membershiprequest ENGINE = InnoDB;
alter table gridgrouper_membershiprequest_history ENGINE = InnoDB;
