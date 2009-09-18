package org.cagrid.tools.events;

import gov.nih.nci.cagrid.common.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseException;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class EventAuditor extends BaseEventHandler implements Auditor {
    


    private Database db;

    private boolean dbBuilt = false;

    private final static String EVENT_ID = "EVENT_ID";
    private final static String TARGET_ID = "TARGET_ID";
    private final static String REPORTING_PARTY_ID = "REPORTING_PARTY_ID";
    private final static String EVENT_TYPE = "EVENT_TYPE";
    private final static String OCCURRED_AT = "OCCURRED_AT";
    private final static String MESSAGE = "MESSAGE";

    private String table;


    public EventAuditor(String name, Database db, String tableName) throws EventHandlerInitializationException {
        super(name);
        try {
            this.db = db;
            db.createDatabaseIfNeeded();
            this.table = tableName;
        } catch (Exception e) {
            throw new EventHandlerInitializationException("Error initializing the event handler, " + name + ": "
                + e.getMessage(), e);
        }
    }


    public void handleEvent(Event event) throws EventHandlingException {
        try {
            insertEvent(event);
        } catch (DatabaseException e) {
            getLog().error(e.getMessage(), e);
            throw new EventHandlingException("An unexpected database error occurred.", e);
        }
    }


    public boolean eventExists(long eventId) throws DatabaseException {
        return db.exists(table, EVENT_ID, eventId);
    }


    public Event getEvent(long eventId) throws DatabaseException {
        Event event = null;
        buildDatabase();
        Connection c = null;

        try {
            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("select * from " + table + " WHERE " + EVENT_ID + "= ?");
            s.setLong(1, eventId);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                event = new Event();
                event.setEventId(rs.getLong(EVENT_ID));
                event.setTargetId(rs.getString(TARGET_ID));
                event.setReportingPartyId(rs.getString(REPORTING_PARTY_ID));
                event.setEventType(rs.getString(EVENT_TYPE));
                event.setOccurredAt(rs.getLong(OCCURRED_AT));
                event.setMessage(rs.getString(MESSAGE));
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new DatabaseException("An unexpected database error occurred.", e);
        } finally {
            db.releaseConnection(c);
        }

        return event;
    }


    public void deleteEvent(long eventId) throws DatabaseException {
        buildDatabase();
        try {
            db.update("delete from " + this.table + " where " + EVENT_ID + "=" + eventId);
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new DatabaseException("An unexpected database error occurred.", e);
        }
    }


    private boolean appendToSQLBuffer(StringBuffer sql, boolean whereAppended, String field) {
        return appendToSQLBuffer(sql, whereAppended, field, "=");
    }


    private boolean appendToSQLBuffer(StringBuffer sql, boolean whereAppended, String field, String operator) {
        if (whereAppended) {
            sql.append(" AND " + field + " " + operator + " ?");
        } else {
            sql.append(" WHERE " + field + " " + operator + " ?");
            whereAppended = true;
        }
        return whereAppended;
    }


    public List<Event> findEvents(String targetId, String reportingPartyId, String eventType, Date start, Date end,
        String message) throws EventAuditingException {
        try {
            buildDatabase();
        } catch (DatabaseException e) {
            throw new EventAuditingException("Unexpected error finding events.", e);
        }
        List<Event> events = new ArrayList<Event>();
        Connection c = null;
        try {
            c = db.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append("select * from " + table);

            boolean whereAppended = false;

            if (targetId != null) {
                whereAppended = appendToSQLBuffer(sql, whereAppended, TARGET_ID);
            }

            if (reportingPartyId != null) {
                whereAppended = appendToSQLBuffer(sql, whereAppended, REPORTING_PARTY_ID);
            }

            if (eventType != null) {
                whereAppended = appendToSQLBuffer(sql, whereAppended, EVENT_TYPE);
            }

            if (start != null) {
                whereAppended = appendToSQLBuffer(sql, whereAppended, OCCURRED_AT, ">=");
            }

            if (end != null) {
                whereAppended = appendToSQLBuffer(sql, whereAppended, OCCURRED_AT, "<=");
            }

            if (message != null) {
                whereAppended = appendToSQLBuffer(sql, whereAppended, MESSAGE, "LIKE");
            }

            PreparedStatement s = c.prepareStatement(sql.toString());
            int count = 1;

            if (targetId != null) {
                s.setString(count, targetId);
                count = count + 1;
            }

            if (reportingPartyId != null) {
                s.setString(count, reportingPartyId);
                count = count + 1;
            }

            if (eventType != null) {
                s.setString(count, eventType);
                count = count + 1;
            }

            if (start != null) {
                s.setLong(count, start.getTime());
                count = count + 1;
            }

            if (end != null) {
                s.setLong(count, end.getTime());
                count = count + 1;
            }

            if (message != null) {
                s.setString(count, "%" + message + "%");
                count = count + 1;
            }

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getLong(EVENT_ID));
                event.setTargetId(rs.getString(TARGET_ID));
                event.setReportingPartyId(rs.getString(REPORTING_PARTY_ID));
                event.setEventType(rs.getString(EVENT_TYPE));
                event.setOccurredAt(rs.getLong(OCCURRED_AT));
                event.setMessage(rs.getString(MESSAGE));
                events.add(event);
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new EventAuditingException("An unexpected database error occurred.", e);
        } finally {
            db.releaseConnection(c);
        }

        return events;
    }


    private Event insertEvent(Event event) throws DatabaseException, EventHandlingException {
        buildDatabase();
        
        if (Utils.clean(event.getTargetId()) == null) {
            
            throw new EventHandlingException("Could not audit event, no target id was specified.");
        }

        if (Utils.clean(event.getReportingPartyId()) == null) {
            throw new EventHandlingException("Could not audit event, no reporting party was specified.");
        }

        if (Utils.clean(event.getEventType()) == null) {
            throw new EventHandlingException("Could not audit event, no event type was specified.");
        }

        if (Utils.clean(event.getMessage()) == null) {
            throw new EventHandlingException("Could not audit event, no event message was specified.");
        }

        if (event.getOccurredAt() <= 0) {
            throw new EventHandlingException("Could not audit event, no occurred at date was specified.");
        }
        Connection c = null;
        try {

            c = db.getConnection();
            PreparedStatement s = c.prepareStatement("INSERT INTO " + this.table + " SET " + TARGET_ID + "= ?, "
                + REPORTING_PARTY_ID + "= ?, " + EVENT_TYPE + "= ?, " + OCCURRED_AT + "= ?, " + MESSAGE + "= ?");

            s.setString(1, event.getTargetId());
            s.setString(2, event.getReportingPartyId());
            s.setString(3, event.getEventType());
            s.setLong(4, event.getOccurredAt());
            s.setString(5, event.getMessage());
            s.execute();
            event.setEventId(db.getLastAutoId(c));
            s.close();
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new DatabaseException("An unexpected database error occurred.", e);
        } finally {
            if (c != null) {
                db.releaseConnection(c);
            }
        }
        return event;
    }


    public void clear() throws EventHandlingException {
        try {
            this.clearDatabase();
        } catch (Exception e) {
            throw new EventHandlingException(Utils.getExceptionMessage(e), e);
        }
    }


    public void clearDatabase() throws DatabaseException {
        buildDatabase();
        db.update("DROP TABLE IF EXISTS " + this.table);
        dbBuilt = false;
    }


    private void buildDatabase() throws DatabaseException {
        if (!dbBuilt) {
            if (!this.db.tableExists(table)) {
                String trust = "CREATE TABLE " + this.table + " (" + EVENT_ID
                    + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY," + TARGET_ID + " VARCHAR(255) NOT NULL,"
                    + REPORTING_PARTY_ID + " VARCHAR(255) NOT NULL," + EVENT_TYPE + " VARCHAR(50) NOT NULL,"
                    + OCCURRED_AT + " BIGINT NOT NULL," + MESSAGE + " TEXT NOT NULL,"
                    + "INDEX document_index (EVENT_ID));";
                db.update(trust);
            }
            dbBuilt = true;
        }
    }
}
