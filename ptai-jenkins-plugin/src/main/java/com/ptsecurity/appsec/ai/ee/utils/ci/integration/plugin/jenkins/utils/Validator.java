package com.ptsecurity.appsec.ai.ee.utils.ci.integration.plugin.jenkins.utils;

import com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings;
import com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.Version;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.Resources;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.domain.AdvancedSettings;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.utils.ReportUtils;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.utils.json.JsonPolicyHelper;
import com.ptsecurity.misc.tools.exceptions.GenericException;
import com.ptsecurity.misc.tools.helpers.UrlHelper;
import hudson.util.FormValidation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import static com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings.Version.V11;
import static com.ptsecurity.appsec.ai.ee.utils.ci.integration.Resources.i18n_ast_settings_type_manual_json_settings_message_invalid;

@Slf4j
public class Validator {
    public static boolean doCheckFieldNotEmpty(String value) {
        return !StringUtils.isEmpty(value);
    }

    protected static boolean checkViaException(@NonNull final Runnable call) {
        try { call.run(); return true; } catch (Exception e) { return false; }
    }

    public static boolean doCheckFieldUrl(String value) {
        return UrlHelper.checkUrl(value);
    }

    public static boolean doCheckFieldInteger(Integer value) {
        return (null != value);
    }

    public static boolean doCheckFieldBetween(Integer value, int from, int to) {
        if (null == value) return false;
        return (from <= value) && (value <= to);
    }

    public static boolean doCheckFieldRegEx(String value) {
        return checkViaException(() -> Pattern.compile(value));
    }

    public static boolean doCheckFieldJsonPolicy(String value) {
        return checkViaException(() -> { if (doCheckFieldNotEmpty(value)) JsonPolicyHelper.verify(value); });
    }

    public static boolean doCheckFieldJsonIssuesFilter(String value) {
        return checkViaException(() -> ReportUtils.validateJsonFilter(value));
    }

    public static boolean doCheckFieldJsonReports(String value) {
        return checkViaException(() -> ReportUtils.validateJsonReports(value));
    }

    public static boolean doCheckFieldAdvancedSettings(String value) {
        return checkViaException(() -> AdvancedSettings.validate(value));
    }

    public static FormValidation doCheckFieldNotEmpty(String value, String errorMessage) {
        return doCheckFieldNotEmpty(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldInteger(Integer value, String errorMessage) {
        return doCheckFieldInteger(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldBetween(Integer value, int from, int to, String errorMessage) {
        return doCheckFieldBetween(value, from, to) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldUrl(String value, String errorMessage) {
        return doCheckFieldUrl(value) ? FormValidation.ok() : FormValidation.warning(errorMessage);
    }

    public static FormValidation doCheckFieldRegEx(String value, String errorMessage) {
        return doCheckFieldRegEx(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldJsonPolicy(String value, String errorMessage) {
        return doCheckFieldJsonPolicy(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldJsonSettings(String value) {
        try {
            UnifiedAiProjScanSettings.ParseResult parseResult = UnifiedAiProjScanSettings.parse(value);
            if (parseResult.getErrors().isEmpty()) {
                FormValidation ok = FormValidation.ok(
                        Resources.i18n_ast_settings_type_manual_json_settings_message_success(
                                parseResult.getSettings().getProjectName(),
                                parseResult.getSettings().getProgrammingLanguage().getValue()));
                if (V11 == parseResult.getSettings().getVersion())
                    return ok;
                else
                    return FormValidation.warning(Resources.i18n_ast_settings_type_manual_json_settings_message_deprecated());
            }
            Collection<FormValidation> errors = new ArrayList<>();
            for (String error : parseResult.getErrors())
                errors.add(FormValidation.error(error));
            return FormValidation.aggregate(errors);
        } catch (GenericException e) {
            return Validator.error(e);
        }
    }

    public static FormValidation doCheckFieldJsonIssuesFilter(String value, String errorMessage) {
        return doCheckFieldJsonIssuesFilter(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldJsonReports(String value, String errorMessage) {
        return doCheckFieldJsonReports(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation doCheckFieldAdvancedSettings(String value, String errorMessage) {
        return doCheckFieldAdvancedSettings(value) ? FormValidation.ok() : FormValidation.error(errorMessage);
    }

    public static FormValidation error(Exception e) {
        // log.log(Level.FINEST, "FormValidation error", e);
        String caption = e.getMessage();
        if (StringUtils.isEmpty(caption))
            return FormValidation.error(e, Resources.i18n_ast_settings_test_message_failed());
        else {
            Throwable cause = e;
            if (e instanceof GenericException) cause = e.getCause();
            return FormValidation.error(cause, Resources.i18n_ast_settings_test_message_failed_details(caption));
        }
    }

    public static FormValidation error(@NonNull final String message, Exception e) {
        // log.log(Level.FINEST, "FormValidation error", e);
        Throwable cause = e;
        if (e instanceof GenericException) cause = e.getCause();
        return FormValidation.error(cause, Resources.i18n_ast_settings_test_message_failed_details(message));
    }

    public static FormValidation error(String message) {
        return FormValidation.error(message);
    }
}
