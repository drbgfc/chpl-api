package gov.healthit.chpl.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.SendMailUtil;
import gov.healthit.chpl.domain.ApiKey;
import gov.healthit.chpl.domain.ApiKeyActivity;
import gov.healthit.chpl.domain.ApiKeyRegistration;
import gov.healthit.chpl.dto.ApiKeyDTO;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.manager.ApiKeyManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "api-key")
@RestController
@RequestMapping("/key")
public class ApiKeyController {

    @Autowired
    private ApiKeyManager apiKeyManager;

    @Autowired
    private SendMailUtil sendMailService;

    @Autowired
    private Environment env;

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Sign up for a new API key.",
            notes = "Anyone wishing to access the methods listed in this API must have an API key. This service "
                    + " will auto-generate a key and send it to the supplied email address. It must be included "
                    + " in subsequent API calls via either a header with the name 'API-Key' or as a URL parameter"
                    + " named 'api_key'.")
    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json; charset=utf-8")
    public String registerDeprecated(@RequestBody final ApiKeyRegistration registration) 
            throws EntityCreationException, AddressException, MessagingException, JsonProcessingException,
            EntityRetrievalException {

        return create(registration);
    }

    @ApiOperation(value = "Sign up for a new API key.",
            notes = "Anyone wishing to access the methods listed in this API must have an API key. This service "
                    + " will auto-generate a key and send it to the supplied email address. It must be included "
                    + " in subsequent API calls via either a header with the name 'API-Key' or as a URL parameter"
                    + " named 'api_key'.")
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json; charset=utf-8")
    public String register(@RequestBody final ApiKeyRegistration registration) throws EntityCreationException,
            AddressException, MessagingException, JsonProcessingException, EntityRetrievalException {

        return create(registration);
    }

    private String create(final ApiKeyRegistration registration) throws EntityCreationException,
            AddressException, MessagingException, JsonProcessingException, EntityRetrievalException {

        Date now = new Date();

        String apiKey = gov.healthit.chpl.util.Util.md5(registration.getName() + registration.getEmail() + now.getTime());
        ApiKeyDTO toCreate = new ApiKeyDTO();

        toCreate.setApiKey(apiKey);
        toCreate.setEmail(registration.getEmail());
        toCreate.setNameOrganization(registration.getName());
        toCreate.setCreationDate(now);
        toCreate.setLastModifiedDate(now);
        toCreate.setLastModifiedUser(-3L);
        toCreate.setDeleted(false);

        apiKeyManager.createKey(toCreate);

        sendRegistrationEmail(registration.getEmail(), registration.getName(), apiKey);

        return "{\"keyRegistered\" : \"" + apiKey + "\"}";
    }


    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Remove an API key.", notes = "This service is only available to CHPL users "
            + "with ROLE_ADMIN.")
    @RequestMapping(value = "/revoke", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String revokeDeprecated(@RequestBody final ApiKey key,
            @RequestHeader(value = "API-Key", required = false) final String userApiKey,
            @RequestParam(value = "apiKey", required = false) final String userApiKeyParam) throws Exception {

        return delete(key, userApiKey, userApiKeyParam);
    }

    @ApiOperation(value = "Remove an API key.", notes = "This service is only available to CHPL users with ROLE_ADMIN.")
    @RequestMapping(value = "/{apiKey}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String revoke(@RequestBody final ApiKey key,
            @RequestHeader(value = "API-Key", required = false) final String userApiKey,
            @RequestParam(value = "apiKey", required = false) final String userApiKeyParam) throws Exception {

        return delete(key, userApiKey, userApiKeyParam);
    }

    private String delete(final ApiKey key, final String userApiKey, final String userApiKeyParam) throws Exception {

        String keyToRevoke = key.getKey();
        if (keyToRevoke.equals(userApiKey) || keyToRevoke.equals(userApiKeyParam)) {
            throw new Exception("A user can not delete their own API key.");
        }
        apiKeyManager.deleteKey(keyToRevoke);
        return "{\"keyRevoked\" : \"" + keyToRevoke + "\"}";

    }

    @ApiOperation(value = "List all API keys that have been created.",
            notes = "This service is only available to CHPL users with ROLE_ADMIN.")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ApiKey> listKeys() {

        List<ApiKey> keys = new ArrayList<ApiKey>();
        List<ApiKeyDTO> dtos = apiKeyManager.findAll();

        for (ApiKeyDTO dto : dtos) {
            ApiKey apiKey = new ApiKey();
            apiKey.setName(dto.getNameOrganization());
            apiKey.setEmail(dto.getEmail());
            apiKey.setKey(dto.getApiKey());
            keys.add(apiKey);
        }

        return keys;
    }

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  View the calls made per API key.",
            notes = "This service is only available to CHPL users with ROLE_ADMIN.")
    @RequestMapping(value = "/activity", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
    public List<ApiKeyActivity> listActivityDeprecated(
            @RequestParam(value = "pageNumber", required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize,
            @RequestParam(value = "filter", required = false) final String apiKeyFilter,
            @RequestParam(value = "dateAscending", required = false) final boolean dateAscending,
            @RequestParam(value = "start", required = false) final Long startDateMilli,
            @RequestParam(value = "end", required = false) final Long endDateMilli) throws EntityRetrievalException {

        return getActivity(pageNumber, pageSize, apiKeyFilter, dateAscending, startDateMilli, endDateMilli);
    }

    @ApiOperation(value = "View the calls made per API key.",
            notes = "This service is only available to CHPL users with ROLE_ADMIN.")
    @RequestMapping(value = "/activity", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
    public List<ApiKeyActivity> listActivity(
            @RequestParam(value = "pageNumber", required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize,
            @RequestParam(value = "filter", required = false) final String apiKeyFilter,
            @RequestParam(value = "dateAscending", required = false) final boolean dateAscending,
            @RequestParam(value = "start", required = false) final Long startDateMilli,
            @RequestParam(value = "end", required = false) final Long endDateMilli) throws EntityRetrievalException {

        return getActivity(pageNumber, pageSize, apiKeyFilter, dateAscending, startDateMilli, endDateMilli);
    }

    private List<ApiKeyActivity> getActivity(Integer pageNumber, Integer pageSize,
            String apiKeyFilter, final boolean dateAscending, final Long startDateMilli,
            final Long endDateMilli) throws EntityRetrievalException {
        if (pageNumber == null) {
            pageNumber = 0;
        }

        if (pageSize == null) {
            pageSize = 100;
        }

        if (apiKeyFilter != null && apiKeyFilter.isEmpty()) {
            apiKeyFilter = null;
        }

        List<ApiKeyActivity> apiKeyActivitiesList = apiKeyManager.getApiKeyActivity(apiKeyFilter, pageNumber, pageSize,
                dateAscending, startDateMilli, endDateMilli);

        return apiKeyActivitiesList;
    }


    @Deprecated
    @ApiOperation(value = "DEPRECATED.  View the calls made by a specific API key.",
            notes = "This service is only available to CHPL users with ROLE_ADMIN.")
    @RequestMapping(value = "/activity/{apiKey}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
    public List<ApiKeyActivity> listActivityByKeyDeprecated(@PathVariable("apiKey") final String apiKey,
            @RequestParam(value = "pageNumber", required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize)
                    throws EntityRetrievalException {

        return getActivityByKey(apiKey, pageNumber, pageSize);
    }

    @ApiOperation(value = "View the calls made by a specific API key.",
            notes = "This service is only available to CHPL users with ROLE_ADMIN.")
    @RequestMapping(value = "/activity/{apiKey}", method = RequestMethod.GET,
                    produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
    public List<ApiKeyActivity> listActivityByKey(@PathVariable("apiKey") final String apiKey,
            @RequestParam(value = "pageNumber", required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize)
                    throws EntityRetrievalException {

        return getActivityByKey(apiKey, pageNumber, pageSize);
    }

    private List<ApiKeyActivity> getActivityByKey(@PathVariable("apiKey") final String apiKey,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize)
                    throws EntityRetrievalException {
        if (pageNumber == null) {
            pageNumber = 0;
        }

        if (pageSize == null) {
            pageSize = 100;
        }

        List<ApiKeyActivity> activity = apiKeyManager.getApiKeyActivity(apiKey, pageNumber, pageSize);

        return activity;

    }

    private void sendRegistrationEmail(final String email, final String orgName, final String apiKey)
            throws AddressException, MessagingException {

        String subject = "CHPL API Key";

        String htmlMessage = "<p>Thank you for registering to use the CHPL API.</p>" + "<p>Your unique API key is: "
                + apiKey + " .</p>"
                + "<p>You'll need to use this unique key each time you access data through our open APIs."
                + "<p>For more information about how to use the API, please visit <a href=\""
                + env.getProperty("chplUrlBegin") + "/#/resources/chpl_api\">"
                + env.getProperty("chplUrlBegin") + "/#/resources/chpl_api</a></p>"
                + "<p>Thanks, <br/>The CHPL Team</p>";

        String[] toEmails = {
                email
        };
        sendMailService.sendEmail(toEmails, null, subject, htmlMessage);
    }
}
