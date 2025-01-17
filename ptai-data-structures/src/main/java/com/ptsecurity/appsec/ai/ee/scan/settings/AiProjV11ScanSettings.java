package com.ptsecurity.appsec.ai.ee.scan.settings;

import com.networknt.schema.ValidationMessage;
import com.ptsecurity.appsec.ai.ee.scan.result.ScanBrief;
import com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings.BlackBoxSettings.FormAuthentication.DetectionType;
import com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.DotNetProjectType;
import com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.JavaVersion;
import com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ProgrammingLanguage;
import com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.blackbox.*;
import com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.siteaddress.Format;
import com.ptsecurity.misc.tools.helpers.ResourcesHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.networknt.schema.ValidatorTypeCode.FORMAT;
import static com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings.JavaSettings.JavaVersion.v1_11;
import static com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings.JavaSettings.JavaVersion.v1_8;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
public class AiProjV11ScanSettings extends UnifiedAiProjScanSettings {
    private static final Map<String, ScanBrief.ScanSettings.Language> PROGRAMMING_LANGUAGE_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, ScanModule> SCAN_MODULE_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, UnifiedAiProjScanSettings.DotNetSettings.ProjectType> DOTNET_PROJECT_TYPE_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, UnifiedAiProjScanSettings.JavaSettings.JavaVersion> JAVA_VERSION_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, BlackBoxSettings.ProxySettings.Type> BLACKBOX_PROXY_TYPE_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, BlackBoxSettings.ScanLevel> BLACKBOX_SCAN_LEVEL_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, BlackBoxSettings.ScanScope> BLACKBOX_SCAN_SCOPE_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, BlackBoxSettings.Authentication.Type> BLACKBOX_AUTH_TYPE_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, BlackBoxSettings.AddressListItem.Format> BLACKBOX_ADDRESS_FORMAT_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));
    private static final Map<String, DetectionType> BLACKBOX_FORM_AUTH_DETECTION_MAP = new TreeMap<>(Comparator.nullsFirst(CASE_INSENSITIVE_ORDER));

    static {
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.JAVA.value(), ScanBrief.ScanSettings.Language.JAVA);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.C_SHARP.value(), ScanBrief.ScanSettings.Language.CSHARP);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.VB.value(), ScanBrief.ScanSettings.Language.VB);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.PHP.value(), ScanBrief.ScanSettings.Language.PHP);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.JAVA_SCRIPT.value(), ScanBrief.ScanSettings.Language.JAVASCRIPT);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.PYTHON.value(), ScanBrief.ScanSettings.Language.PYTHON);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.OBJECTIVE_C.value(), ScanBrief.ScanSettings.Language.OBJECTIVEC);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.SWIFT.value(), ScanBrief.ScanSettings.Language.SWIFT);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.C_AND_C_PLUS_PLUS.value(), ScanBrief.ScanSettings.Language.CPP);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.GO.value(), ScanBrief.ScanSettings.Language.GO);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.KOTLIN.value(), ScanBrief.ScanSettings.Language.KOTLIN);
        PROGRAMMING_LANGUAGE_MAP.put(ProgrammingLanguage.SQL.value(), ScanBrief.ScanSettings.Language.SQL);

        SCAN_MODULE_MAP.put(com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ScanModule.CONFIGURATION.value(), ScanModule.CONFIGURATION);
        SCAN_MODULE_MAP.put(com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ScanModule.COMPONENTS.value(), ScanModule.COMPONENTS);
        SCAN_MODULE_MAP.put(com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ScanModule.BLACK_BOX.value(), ScanModule.BLACKBOX);
        SCAN_MODULE_MAP.put(com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ScanModule.PATTERN_MATCHING.value(), ScanModule.PATTERNMATCHING);
        SCAN_MODULE_MAP.put(com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ScanModule.DATA_FLOW_ANALYSIS.value(), ScanModule.DATAFLOWANALYSIS);
        SCAN_MODULE_MAP.put(com.ptsecurity.appsec.ai.ee.scan.settings.aiproj.v11.ScanModule.VULNERABLE_SOURCE_CODE.value(), ScanModule.VULNERABLESOURCECODE);

        DOTNET_PROJECT_TYPE_MAP.put(DotNetProjectType.NONE.value(), DotNetSettings.ProjectType.NONE);
        DOTNET_PROJECT_TYPE_MAP.put(DotNetProjectType.SOLUTION.value(), DotNetSettings.ProjectType.SOLUTION);
        DOTNET_PROJECT_TYPE_MAP.put(DotNetProjectType.WEB_SITE.value(), DotNetSettings.ProjectType.WEBSITE);

        JAVA_VERSION_MAP.put(JavaVersion.V_1_8.value(), v1_8);
        JAVA_VERSION_MAP.put(JavaVersion.V_1_11.value(), v1_11);

        BLACKBOX_PROXY_TYPE_MAP.put(ProxyType.HTTP.value(), BlackBoxSettings.ProxySettings.Type.HTTP);
        BLACKBOX_PROXY_TYPE_MAP.put(ProxyType.SOCKS_4.value(), BlackBoxSettings.ProxySettings.Type.SOCKS4);
        BLACKBOX_PROXY_TYPE_MAP.put(ProxyType.SOCKS_5.value(), BlackBoxSettings.ProxySettings.Type.SOCKS5);

        BLACKBOX_SCAN_LEVEL_MAP.put(ScanLevel.NONE.value(), BlackBoxSettings.ScanLevel.NONE);
        BLACKBOX_SCAN_LEVEL_MAP.put(ScanLevel.FAST.value(), BlackBoxSettings.ScanLevel.FAST);
        BLACKBOX_SCAN_LEVEL_MAP.put(ScanLevel.FULL.value(), BlackBoxSettings.ScanLevel.FULL);
        BLACKBOX_SCAN_LEVEL_MAP.put(ScanLevel.NORMAL.value(), BlackBoxSettings.ScanLevel.NORMAL);

        BLACKBOX_SCAN_SCOPE_MAP.put(ScanScope.PATH.value(), BlackBoxSettings.ScanScope.PATH);
        BLACKBOX_SCAN_SCOPE_MAP.put(ScanScope.DOMAIN.value(), BlackBoxSettings.ScanScope.DOMAIN);
        BLACKBOX_SCAN_SCOPE_MAP.put(ScanScope.FOLDER.value(), BlackBoxSettings.ScanScope.FOLDER);

        BLACKBOX_AUTH_TYPE_MAP.put(AuthType.NONE.value(), BlackBoxSettings.Authentication.Type.NONE);
        BLACKBOX_AUTH_TYPE_MAP.put(AuthType.FORM.value(), BlackBoxSettings.Authentication.Type.FORM);
        BLACKBOX_AUTH_TYPE_MAP.put(AuthType.RAW_COOKIE.value(), BlackBoxSettings.Authentication.Type.COOKIE);
        BLACKBOX_AUTH_TYPE_MAP.put(AuthType.HTTP.value(), BlackBoxSettings.Authentication.Type.HTTP);

        BLACKBOX_ADDRESS_FORMAT_MAP.put(Format.WILDCARD.value(), BlackBoxSettings.AddressListItem.Format.WILDCARD);
        BLACKBOX_ADDRESS_FORMAT_MAP.put(Format.EXACT_MATCH.value(), BlackBoxSettings.AddressListItem.Format.EXACTMATCH);
        BLACKBOX_ADDRESS_FORMAT_MAP.put(Format.REG_EXP.value(), BlackBoxSettings.AddressListItem.Format.REGEXP);

        BLACKBOX_FORM_AUTH_DETECTION_MAP.put(AuthFormDetectionType.AUTO.value(), DetectionType.AUTO);
        BLACKBOX_FORM_AUTH_DETECTION_MAP.put(AuthFormDetectionType.MANUAL.value(), DetectionType.MANUAL);
    }

    @Override
    public @NonNull String getJsonSchema() {
        return ResourcesHelper.getResourceString("aiproj/schema/aiproj-v1.1.json");
    }

    @Override
    public void processErrorMessages(Set<ValidationMessage> errors) {
        errors.removeIf(e -> {
            return
                    e.getCode().equals(FORMAT.getErrorCode()) &&
                    e.getSchemaPath().equals("#/properties/MailingProjectSettings/properties/EmailRecipients/items");
        });
    }

    @Override
    public Version getVersion() {
        return Version.V11;
    }

    @Override
    public @NonNull String getProjectName() {
        return S("$.ProjectName");
    }

    @Override
    public @NonNull ScanBrief.ScanSettings.Language getProgrammingLanguage() {
        return PROGRAMMING_LANGUAGE_MAP.get(S("$.ProgrammingLanguage"));
    }

    @Override
    public UnifiedAiProjScanSettings setProgrammingLanguage(ScanBrief.ScanSettings.@NonNull Language value) {
        for (String language : PROGRAMMING_LANGUAGE_MAP.keySet()) {
            if (!PROGRAMMING_LANGUAGE_MAP.get(language).equals(value)) continue;
            aiprojDocument.put("$", "ProgrammingLanguage", language);
            break;
        }
        return this;
    }

    @Override
    public Set<ScanModule> getScanModules() {
        Set<ScanModule> res = new HashSet<>();
        List<String> scanModules = O("$.ScanModules");
        for (String scanModule : scanModules)
            if (SCAN_MODULE_MAP.containsKey(scanModule)) res.add(SCAN_MODULE_MAP.get(scanModule));
        return res;
    }

    @Override
    public UnifiedAiProjScanSettings setScanModules(@NonNull Set<ScanModule> modules) {
        aiprojDocument.put("$", "ScanModules", modules);
        return this;
    }

    @Override
    public String getCustomParameters() {
        return S("$.CustomParameters");
    }

    @Override
    public UnifiedAiProjScanSettings setCustomParameters(String parameters) {
        aiprojDocument.put("$", "CustomParameters", parameters);
        return this;
    }

    @Override
    public DotNetSettings getDotNetSettings() {
        if (null == O(aiprojDocument, "$.DotNetSettings")) return null;
        String solutionFile = S("$.DotNetSettings.SolutionFile");
        String projectType = S("$.DotNetSettings.ProjectType");
        return DotNetSettings.builder()
                .solutionFile(fixSolutionFile(solutionFile))
                .projectType(DOTNET_PROJECT_TYPE_MAP.getOrDefault(projectType, DotNetSettings.ProjectType.NONE))
                .build();
    }

    @Override
    public JavaSettings getJavaSettings() {
        if (null == O(aiprojDocument, "$.JavaSettings")) return null;
        return JavaSettings.builder()
                .unpackUserPackages(B("$.JavaSettings.UnpackUserPackages"))
                .userPackagePrefixes(S("$.JavaSettings.UserPackagePrefixes"))
                .javaVersion(JAVA_VERSION_MAP.getOrDefault(S("$.JavaSettings.Version"), v1_11))
                .parameters(S("$.JavaSettings.Parameters"))
                .build();
    }

    @Override
    public @NonNull Boolean isSkipGitIgnoreFiles() {
        return B("$.SkipGitIgnoreFiles");
    }

    @Override
    public @NonNull Boolean isUsePublicAnalysisMethod() {
        return B("$.UsePublicAnalysisMethod");
    }

    @Override
    public UnifiedAiProjScanSettings setUsePublicAnalysisMethod(@NonNull Boolean value) {
        aiprojDocument.put("$", "UsePublicAnalysisMethod", value);
        return this;
    }

    @Override
    public @NonNull Boolean isUseSastRules() {
        return B("$.UseSastRules");
    }

    @Override
    public @NonNull Boolean isUseCustomPmRules() {
        return B("$.UseCustomPmRules");
    }

    @Override
    public @NonNull Boolean isUseCustomYaraRules() {
        log.trace("No custom SAST rules support for AIPROJ schema v.1.1");
        return false;
    }

    @Override
    public @NonNull Boolean isUseSecurityPolicies() {
        return B("$.UseSecurityPolicies");
    }

    @Override
    public @NonNull Boolean isDownloadDependencies() {
        return B("$.DownloadDependencies");
    }

    @Override
    public UnifiedAiProjScanSettings setDownloadDependencies(@NonNull Boolean value) {
        aiprojDocument.put("$", "DownloadDependencies", value);
        return this;
    }

    @Override
    public MailingProjectSettings getMailingProjectSettings() {
        Object mailingProjectSettings = O("$.MailingProjectSettings");
        if (null == mailingProjectSettings) return null;

        return MailingProjectSettings.builder()
                .enabled(B(mailingProjectSettings, "$.Enabled"))
                .mailProfileName(S(mailingProjectSettings, "$.MailProfileName"))
                .emailRecipients(O(mailingProjectSettings, "$.EmailRecipients"))
                .build();
    }

    private BlackBoxSettings.ProxySettings convertProxySettings(@NonNull final Object proxySettings) {
        return BlackBoxSettings.ProxySettings.builder()
                .enabled(B(proxySettings, "$.Enabled"))
                .type(BLACKBOX_PROXY_TYPE_MAP.get(S(proxySettings, "$.Type")))
                .host(S(proxySettings, "$.Host"))
                .port(I(proxySettings, "$.Port"))
                .login(S(proxySettings, "$.Login"))
                .password(S(proxySettings, "$.Password"))
                .build();
    }

    private BlackBoxSettings.Authentication convertAuthentication(final Object auth) {
        log.trace("Check if AIPROJ authentication field is defined");
        if (null == auth) {
            log.info("Explicitly set authentication type NONE as there's no authentication settings defined");
            return BlackBoxSettings.Authentication.NONE;
        }
        BlackBoxSettings.Authentication.Type authType;
        authType = BLACKBOX_AUTH_TYPE_MAP.getOrDefault(S(auth, "$.Type"), BlackBoxSettings.Authentication.Type.NONE);

        if (BlackBoxSettings.Authentication.Type.FORM == authType) {
            Object form = O(auth, "$.Form");
            if (null == form) {
                log.info("Explicitly set authentication type NONE as there's no form authentication settings defined");
                return BlackBoxSettings.Authentication.NONE;
            }
            DetectionType detectionType = BLACKBOX_FORM_AUTH_DETECTION_MAP.getOrDefault(S(form, "$.FormDetection"), DetectionType.AUTO);
            return BlackBoxSettings.FormAuthentication.builder()
                    .type(authType)
                    .detectionType(detectionType)
                    .loginKey(S(form, "$.LoginKey"))
                    .passwordKey(S(form, "$.PasswordKey"))
                    .login(S(form, "$.Login"))
                    .password(S(form, "$.Password"))
                    .formAddress(S(form, "$.FormAddress"))
                    .xPath(S(form, "$.FormXPath"))
                    .validationTemplate(S(form, "$.ValidationTemplate"))
                    .build();
        } else if (BlackBoxSettings.Authentication.Type.HTTP == authType) {
            Object http = O(auth, "$.Http");
            if (null == http) {
                log.info("Explicitly set authentication type NONE as there's no HTTP authentication settings defined");
                return BlackBoxSettings.Authentication.NONE;
            }
            return BlackBoxSettings.HttpAuthentication.builder()
                    .login(S(http, "$.Login"))
                    .password(S(http, "$.Password"))
                    .validationAddress(S(http, "$.ValidationAddress"))
                    .build();
        } else if (BlackBoxSettings.Authentication.Type.COOKIE == authType) {
            Object cookie = O(auth, "$.Cookie");
            if (null == cookie) {
                log.info("Explicitly set authentication type NONE as there's no cookie authentication settings defined");
                return BlackBoxSettings.Authentication.NONE;
            }
            return BlackBoxSettings.CookieAuthentication.builder()
                    .cookie(S(cookie, "$.Cookie"))
                    .validationAddress(S(cookie, "$.ValidationAddress"))
                    .validationTemplate(S(cookie, "$.ValidationTemplate"))
                    .build();
        } else
            return BlackBoxSettings.Authentication.NONE;
    }

    private List<Pair<String, String>> convertHeaders(@NonNull final List<Object> headers) {
        List<Pair<String, String>> res = new ArrayList<>();

        for (Object headerKeyValue : headers) {
            String key = S(headerKeyValue, "$.Key");
            if (isEmpty(key)) {
                log.trace("Skip header with empty name");
                continue;
            }
            res.add(new ImmutablePair<>(key, S(headerKeyValue, "$.Value")));
        }
        return CollectionUtils.isEmpty(res) ? null : res;
    }

    private List<BlackBoxSettings.AddressListItem> convertAddresses(@NonNull final List<Object> addresses) {
        List<BlackBoxSettings.AddressListItem> res = new ArrayList<>();

        for (Object address : addresses) {
            String stringFormat = S(address, "$.Format");
            BlackBoxSettings.AddressListItem.Format format = BLACKBOX_ADDRESS_FORMAT_MAP.get(stringFormat);
            if (null == format) {
                log.trace("Skip unknown address format {}", stringFormat);
                continue;
            }
            res.add(new BlackBoxSettings.AddressListItem(format, S(address, "$.Address")));
        }
        return CollectionUtils.isEmpty(res) ? null : res;
    }

    @Override
    public BlackBoxSettings getBlackBoxSettings() {
        if (!getScanModules().contains(ScanModule.BLACKBOX)) return null;
        Object blackBoxSettings = O("$.BlackBoxSettings");
        if (null == blackBoxSettings) return null;

        BlackBoxSettings res = new BlackBoxSettings();

        res.setScanLevel(BLACKBOX_SCAN_LEVEL_MAP.getOrDefault(S("$.BlackBoxSettings.Level"), BlackBoxSettings.ScanLevel.NONE));
        res.setRunAutocheckAfterScan(B(blackBoxSettings, "$.RunAutocheckAfterScan"));
        res.setSite(S(blackBoxSettings, "$.Site"));
        res.setScanScope(BLACKBOX_SCAN_SCOPE_MAP.getOrDefault(S("$.BlackBoxSettings.ScanScope"), BlackBoxSettings.ScanScope.PATH));
        res.setSslCheck(B(blackBoxSettings, "$.SslCheck"));

        Object proxySettings = O(blackBoxSettings, "$.ProxySettings");
        if (null != proxySettings)
            res.setProxySettings(convertProxySettings(proxySettings));

        List<Object> customHeaders = O(blackBoxSettings, "$.AdditionalHttpHeaders");
        if (null != customHeaders && 0 != customHeaders.size())
            res.setHttpHeaders(convertHeaders(customHeaders));

        Object authentication = O(blackBoxSettings, "$.Authentication");
        res.setAuthentication(convertAuthentication(authentication));

        List<Object> addresses = O(blackBoxSettings, "$.WhiteListedAddresses");
        if (null != addresses && 0 != addresses.size())
            res.setWhiteListedAddresses(convertAddresses(addresses));
        addresses = O(blackBoxSettings, "$.BlackListedAddresses");
        if (null != addresses && 0 != addresses.size())
            res.setBlackListedAddresses(convertAddresses(addresses));

        return res;
    }
}
