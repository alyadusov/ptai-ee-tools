package com.ptsecurity.appsec.ai.ee.utils.ci.integration.plugin.jenkins.scansettings;

import com.ptsecurity.appsec.ai.ee.scan.settings.Policy;
import com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.Resources;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.plugin.jenkins.utils.Validator;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.utils.json.JsonPolicyHelper;
import hudson.Extension;
import hudson.model.Item;
import hudson.util.FormValidation;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.ArrayList;
import java.util.Collection;

@ToString
public class ScanSettingsManual extends com.ptsecurity.appsec.ai.ee.utils.ci.integration.plugin.jenkins.scansettings.ScanSettings {
    @Getter
    private final String jsonSettings;

    @Getter
    private final String jsonPolicy;

    @DataBoundConstructor
    public ScanSettingsManual(
            final String jsonSettings, final String jsonPolicy) {
        this.jsonSettings = jsonSettings;
        this.jsonPolicy = jsonPolicy;
    }

    @Extension
    @Symbol("scanSettingsManual")
    public static class Descriptor extends ScanSettingsDescriptor {
        @Override
        @NonNull
        public String getDisplayName() {
            return Resources.i18n_ast_settings_type_manual_label();
        }

        public FormValidation doCheckJsonSettings(@QueryParameter String value) {
            return Validator.doCheckFieldJsonSettings(value);
        }

        public FormValidation doCheckJsonPolicy(@QueryParameter String value) {
            if (Validator.doCheckFieldNotEmpty(value))
                return Validator.doCheckFieldJsonPolicy(value, Resources.i18n_ast_settings_type_manual_json_policy_message_invalid());
            else
                return FormValidation.ok();
        }

        public FormValidation doTestJsonSettings(
                @AncestorInPath Item ignoredItem,
                @QueryParameter("jsonSettings") final String jsonSettings) {
            return Validator.doCheckFieldJsonSettings(jsonSettings);
        }

        public FormValidation doTestJsonPolicy(
                @AncestorInPath Item ignoredItem,
                @QueryParameter("jsonPolicy") final String jsonPolicy) {
            try {
                if (!Validator.doCheckFieldNotEmpty(jsonPolicy))
                    return FormValidation.ok(Resources.i18n_ast_settings_type_manual_json_policy_message_empty());

                Policy[] policy = JsonPolicyHelper.verify(jsonPolicy);
                if (null == policy || 0 == policy.length)
                    return FormValidation.ok(Resources.i18n_ast_settings_type_manual_json_policy_message_empty());
                else
                    return FormValidation.ok(Resources.i18n_ast_settings_type_manual_json_policy_message_success(policy.length));
            } catch (Exception e) {
                return Validator.error(e);
            }
        }
    }
}
