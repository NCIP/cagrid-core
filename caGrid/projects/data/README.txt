To build the required beans, type 'ant jarStubs' at the project's top level.

This project now requires Java 1.5 to build; it depends on Java 5 libraries, and contains Java 5 code

Source folders:
	Service: server-side implementation
	Utilities: Stuff that makes clients or service implementors lives easier
	Validation: Validation of CQL against domain models or the CQL schema
	Common: Common interfaces for both service and client
	CQL: CQL query processor base classes and exceptions
	Tools: "External" tools that are related to or work with data services, but aren't part of the infrastructure
	
	Potential new source folder: Clients: service clients
	