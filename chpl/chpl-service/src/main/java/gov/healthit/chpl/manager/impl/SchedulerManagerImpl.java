package gov.healthit.chpl.manager.impl;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.domain.concept.ScheduleTypeConcept;
import gov.healthit.chpl.domain.schedule.ChplJob;
import gov.healthit.chpl.domain.schedule.ChplTrigger;
import gov.healthit.chpl.exception.ValidationException;
import gov.healthit.chpl.manager.SchedulerManager;

/**
 * Implementation of Scheduler Manager.
 * @author alarned
 *
 */
@Service
public class SchedulerManagerImpl implements SchedulerManager {
    public static final String AUTHORITY_DELIMITER = ";";
    
    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ChplTrigger createTrigger(final ChplTrigger trigger) throws SchedulerException, ValidationException {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerId;
        JobKey jobId;
        switch (trigger.getScheduleType()) {
        case CACHE_STATUS_AGE_NOTIFICATION:
            triggerId = triggerKey(trigger.getEmail().replaceAll("\\.",  "_"), "cacheStatusAgeTrigger");
            jobId = jobKey("cacheStatusAgeJob", "chplJobs");
            break;
        default:
            throw new ValidationException("invalid data");
        }

        Trigger qzTrigger = newTrigger()
                .withIdentity(triggerId)
                .startNow()
                .forJob(jobId)
                .usingJobData("email", trigger.getEmail())
                .withSchedule(cronSchedule(trigger.getCronSchedule()))
                .build();
        scheduler.scheduleJob(qzTrigger);

        ChplTrigger newTrigger = new ChplTrigger((CronTrigger) scheduler.getTrigger(triggerId));
        newTrigger.setScheduleType(ScheduleTypeConcept.CACHE_STATUS_AGE_NOTIFICATION);
        return newTrigger;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteTrigger(final String scheduleType, final String triggerId)
            throws SchedulerException, ValidationException {
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey;
        switch (scheduleType) {
        case "CACHE_STATUS_AGE_NOTIFICATION":
            triggerKey = triggerKey(triggerId, "cacheStatusAgeTrigger");
            break;
        default:
            throw new ValidationException("invalid data");
        }
        scheduler.unscheduleJob(triggerKey);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<ChplTrigger> getAllTriggers() throws SchedulerException {
        ArrayList<ChplTrigger> triggers = new ArrayList<ChplTrigger>();
        Scheduler scheduler = getScheduler();
        for (String group: scheduler.getTriggerGroupNames()) {
            // enumerate each trigger in group
            for (TriggerKey triggerKey : scheduler.getTriggerKeys(groupEquals(group))) {
                if (scheduler.getTrigger(triggerKey).getJobKey().getGroup().equalsIgnoreCase("chplJobs")) {
                    ChplTrigger newTrigger = new ChplTrigger((CronTrigger) scheduler.getTrigger(triggerKey));
                    switch (newTrigger.getJobName()) {
                    case "cacheStatusAgeJob":
                        newTrigger.setScheduleType(ScheduleTypeConcept.CACHE_STATUS_AGE_NOTIFICATION);
                        break;
                    default:
                        break;
                    }
                    triggers.add(newTrigger);
                }
            }
        }
        return triggers;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ChplTrigger updateTrigger(final ChplTrigger trigger) throws SchedulerException, ValidationException {
        Scheduler scheduler = getScheduler();
        Trigger oldTrigger;
        switch (trigger.getScheduleType()) {
        case CACHE_STATUS_AGE_NOTIFICATION:
            oldTrigger = scheduler.getTrigger(triggerKey(trigger.getName(), "cacheStatusAgeTrigger"));
            break;
        default:
            throw new ValidationException("invalid data");
        }
        Trigger qzTrigger = newTrigger()
                .withIdentity(oldTrigger.getKey())
                .startNow()
                .forJob(oldTrigger.getJobKey())
                .usingJobData(oldTrigger.getJobDataMap())
                .withSchedule(cronSchedule(trigger.getCronSchedule()))
                .build();
        scheduler.rescheduleJob(oldTrigger.getKey(), qzTrigger);

        ChplTrigger newTrigger = new ChplTrigger((CronTrigger) qzTrigger);
        newTrigger.setScheduleType(ScheduleTypeConcept.CACHE_STATUS_AGE_NOTIFICATION);
        return newTrigger;
    }

    
    /* (non-Javadoc)
     * @see gov.healthit.chpl.manager.SchedulerManager#getAllJobs()
     * As new jobs are added that have authorities other than ROLE_ADMIN, those authorities
     * will need to be added to the list. 
     */
    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<ChplJob> getAllJobs() throws SchedulerException {
        List<ChplJob> jobs = new ArrayList<ChplJob>();
        Scheduler scheduler = getScheduler();
        for (String group : scheduler.getJobGroupNames()) {
            if (group.equals("chplJobs")) {
                for (JobKey jobKey : scheduler.getJobKeys(groupEquals(group))) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    if (doesUserHavePermissionToJob(jobDetail)) {
                        jobs.add(new ChplJob(jobDetail));
                    }
                }
            }
        }
        return jobs;
    }

    private Scheduler getScheduler() throws SchedulerException {
        StdSchedulerFactory sf = new StdSchedulerFactory();
        sf.initialize("quartz.properties");
        Scheduler scheduler = sf.getScheduler();
        return scheduler;
    }
    
    private Boolean doesUserHavePermissionToJob(JobDetail jobDetail) {
        //Get the authorities from the job
        if (jobDetail.getJobDataMap().containsKey("authorities")) {
            List<String> authorities = new ArrayList<String>(
                    Arrays.asList(jobDetail.getJobDataMap().get("authorities").toString().split(AUTHORITY_DELIMITER)));
            Set<GrantedPermission> userRoles = Util.getCurrentUser().getPermissions();
            for (GrantedPermission permission : userRoles) {
                for (String authority : authorities) {
                    if (permission.getAuthority().equalsIgnoreCase(authority)) {
                        return true;
                    }
                }
            }
            
        } else {
            //If no authorities are present, we assume there are no permissions on the job
            //and everyone has access
            return true;
        }
        //At this point we have fallen through all of the logic, and the user does not have the appropriate rights
        return false;
    }
}
