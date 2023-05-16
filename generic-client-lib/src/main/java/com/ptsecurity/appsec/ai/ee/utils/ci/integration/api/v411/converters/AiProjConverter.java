package com.ptsecurity.appsec.ai.ee.utils.ci.integration.api.v411.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptsecurity.appsec.ai.ee.scan.result.ScanBrief;
import com.ptsecurity.appsec.ai.ee.scan.result.ScanResult;
import com.ptsecurity.appsec.ai.ee.scan.settings.Policy;
import com.ptsecurity.appsec.ai.ee.scan.settings.UnifiedAiProjScanSettings;
import com.ptsecurity.appsec.ai.ee.server.v411.projectmanagement.model.*;
import com.ptsecurity.misc.tools.exceptions.GenericException;
import com.ptsecurity.appsec.ai.ee.utils.ci.integration.utils.json.JsonPolicyHelper;
import com.ptsecurity.misc.tools.helpers.BaseJsonHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ptsecurity.appsec.ai.ee.scan.settings.AbstractAiProjScanSettings.ScanAppType.DEPENDENCYCHECK;
import static com.ptsecurity.appsec.ai.ee.scan.settings.AbstractAiProjScanSettings.ScanAppType.FINGERPRINT;
import static com.ptsecurity.misc.tools.helpers.BaseJsonHelper.createObjectMapper;
import static com.ptsecurity.misc.tools.helpers.CallHelper.call;

@Slf4j
public class AiProjConverter {
    private static final Map<UnifiedAiProjScanSettings.BlackBoxScanLevel, BlackBoxScanLevel> BLACKBOX_SCAN_LEVEL_MAP = new HashMap<>();
    private static final Map<UnifiedAiProjScanSettings.BlackBoxScanScope, ScanScope> BLACKBOX_SCAN_SCOPE_MAP = new HashMap<>();
    private static final Map<UnifiedAiProjScanSettings.Authentication.Item.Credentials.Type, AuthType> BLACKBOX_AUTH_TYPE_MAP = new HashMap<>();
    private static final Map<UnifiedAiProjScanSettings.ProxySettings.Type, ProxyType> BLACKBOX_PROXY_TYPE_MAP = new HashMap<>();
    private static final Map<ScanResult.ScanSettings.Language, ProgrammingLanguageGroup> REVERSE_LANGUAGE_GROUP_MAP = new HashMap<>();
    private static final Map<UnifiedAiProjScanSettings.DotNetSettings.ProjectType, DotNetProjectType> DOTNET_PROJECT_TYPE_MAP = new HashMap<>();
    private static final Map<UnifiedAiProjScanSettings.JavaSettings.JavaVersion, JavaVersions> JAVA_VERSION_MAP = new HashMap<>();


    static {
        BLACKBOX_SCAN_LEVEL_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanLevel.NONE, BlackBoxScanLevel.NONE);
        BLACKBOX_SCAN_LEVEL_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanLevel.FAST, BlackBoxScanLevel.FAST);
        BLACKBOX_SCAN_LEVEL_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanLevel.NORMAL, BlackBoxScanLevel.NORMAL);
        BLACKBOX_SCAN_LEVEL_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanLevel.FULL, BlackBoxScanLevel.FULL);

        BLACKBOX_SCAN_SCOPE_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanScope.DOMAIN, ScanScope.DOMAIN);
        BLACKBOX_SCAN_SCOPE_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanScope.FOLDER, ScanScope.FOLDER);
        BLACKBOX_SCAN_SCOPE_MAP.put(UnifiedAiProjScanSettings.BlackBoxScanScope.PATH, ScanScope.PATH);

        BLACKBOX_AUTH_TYPE_MAP.put(UnifiedAiProjScanSettings.Authentication.Item.Credentials.Type.FORM, AuthType.FORM);
        BLACKBOX_AUTH_TYPE_MAP.put(UnifiedAiProjScanSettings.Authentication.Item.Credentials.Type.HTTP, AuthType.HTTP);
        BLACKBOX_AUTH_TYPE_MAP.put(UnifiedAiProjScanSettings.Authentication.Item.Credentials.Type.NONE, AuthType.NONE);
        BLACKBOX_AUTH_TYPE_MAP.put(UnifiedAiProjScanSettings.Authentication.Item.Credentials.Type.COOKIE, AuthType.RAWCOOKIE);

        BLACKBOX_PROXY_TYPE_MAP.put(UnifiedAiProjScanSettings.ProxySettings.Type.HTTP, ProxyType.HTTP);
        BLACKBOX_PROXY_TYPE_MAP.put(UnifiedAiProjScanSettings.ProxySettings.Type.HTTPNOCONNECT, ProxyType.HTTPNOCONNECT);
        BLACKBOX_PROXY_TYPE_MAP.put(UnifiedAiProjScanSettings.ProxySettings.Type.SOCKS4, ProxyType.SOCKS4);
        BLACKBOX_PROXY_TYPE_MAP.put(UnifiedAiProjScanSettings.ProxySettings.Type.SOCKS5, ProxyType.SOCKS5);

        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.CPP, ProgrammingLanguageGroup.CANDCPLUSPLUS);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.GO, ProgrammingLanguageGroup.GO);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.JAVASCRIPT, ProgrammingLanguageGroup.JAVASCRIPT);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.CSHARP, ProgrammingLanguageGroup.CSHARP);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.JAVA, ProgrammingLanguageGroup.JAVA);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.KOTLIN, ProgrammingLanguageGroup.KOTLIN);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.SQL, ProgrammingLanguageGroup.SQL);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.PYTHON, ProgrammingLanguageGroup.PYTHON);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.SWIFT, ProgrammingLanguageGroup.SWIFT);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.VB, ProgrammingLanguageGroup.VB);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.PHP, ProgrammingLanguageGroup.PHP);
        REVERSE_LANGUAGE_GROUP_MAP.put(ScanBrief.ScanSettings.Language.OBJECTIVEC, ProgrammingLanguageGroup.OBJECTIVEC);

        DOTNET_PROJECT_TYPE_MAP.put(UnifiedAiProjScanSettings.DotNetSettings.ProjectType.NONE, DotNetProjectType.NONE);
        DOTNET_PROJECT_TYPE_MAP.put(UnifiedAiProjScanSettings.DotNetSettings.ProjectType.SOLUTION, DotNetProjectType.SOLUTION);
        DOTNET_PROJECT_TYPE_MAP.put(UnifiedAiProjScanSettings.DotNetSettings.ProjectType.WEBSITE, DotNetProjectType.WEBSITE);

        JAVA_VERSION_MAP.put(UnifiedAiProjScanSettings.JavaSettings.JavaVersion.v1_8, JavaVersions.v1_8);
        JAVA_VERSION_MAP.put(UnifiedAiProjScanSettings.JavaSettings.JavaVersion.v1_11, JavaVersions.v1_11);
    }

    protected static WhiteBoxSettingsModel apply(@NonNull final UnifiedAiProjScanSettings settings, @NonNull WhiteBoxSettingsModel model) {
        log.trace("Parse AIPROJ vulnerability search modules list");
        // Vulnerability search modules. Possible values are: Php, Java, CSharp, Configuration,
        // Fingerprint (includes DependencyCheck), PmTaint , BlackBox, JavaScript
        Set<UnifiedAiProjScanSettings.ScanAppType> scanAppTypes = Arrays.stream(settings.getScanAppType().split("[, ]+"))
                .map(AiProjScanSettings.ScanAppType::from)
                .collect(Collectors.toSet());

        log.trace("Set base project whitebox settings");
        // "Vulnerable source code" checkbox means that we either enabled AI-supported PHP / Java / C# / JS scan mode ...
        boolean checkScanAppTypeResult = scanAppTypes.stream().anyMatch(SCAN_APP_TYPE_AI::contains);
        // ... or all other languages with PmTaint / UseTaintAnalysis enabled
        boolean checkTaintOnlyLanguage = !LANGUAGE_AI.contains(settings.getProgrammingLanguage()) &&
                scanAppTypes.contains(AiProjScanSettings.ScanAppType.PMTAINT) &&
                null != settings.getUseTaintAnalysis() && settings.getUseTaintAnalysis();
        model.setSearchForVulnerableSourceCodeEnabled(checkScanAppTypeResult || checkTaintOnlyLanguage);
        model.setDataFlowAnalysisEnabled(null != settings.getUseTaintAnalysis() && settings.getUseTaintAnalysis() && scanAppTypes.contains(AiProjScanSettings.ScanAppType.PMTAINT));
        model.setPatternMatchingEnabled(null != settings.getUsePmAnalysis() && settings.getUsePmAnalysis() && scanAppTypes.contains(AiProjScanSettings.ScanAppType.PMTAINT));
        model.setSearchForConfigurationFlawsEnabled(scanAppTypes.contains(AiProjScanSettings.ScanAppType.CONFIGURATION));
        model.setSearchForVulnerableComponentsEnabled(scanAppTypes.contains(FINGERPRINT) || scanAppTypes.contains(DEPENDENCYCHECK));

        return model;
    }

    /**
     * PT AI project creation is to be started with POST API call with base project settings. This method
     * uses default base settings as a template and applies AIPROJ scan settings to them
     * @param settings AIPROJ settings to be applied to default settings
     * @param defaultSettings Default settings that PT AI API provides
     * @return
     */
    @SneakyThrows
    public static BaseProjectSettingsModel convert(
            @NonNull final AiProjScanSettings settings,
            @NonNull final BaseProjectSettingsModel defaultSettings) {
        // Create deep settings copy
        ObjectMapper objectMapper = new ObjectMapper();
        BaseProjectSettingsModel result = objectMapper.readValue(objectMapper.writeValueAsString(defaultSettings), BaseProjectSettingsModel.class);

        log.trace("Set base project settings");
        result.setName(settings.getProjectName());
        result.setProgrammingLanguageGroup(convertLanguageGroup(settings.getProgrammingLanguage()));
        result.setProjectUrl(settings.getSite());

        result.setWhiteBox(apply(settings, new WhiteBoxSettingsModel()));

        // Vulnerability search modules. Possible values are: Php, Java, CSharp, Configuration,
        // Fingerprint (includes DependencyCheck), PmTaint , BlackBox, JavaScript
        Set<AiProjScanSettings.ScanAppType> scanAppTypes = Arrays.stream(settings.getScanAppType().split("[, ]+"))
                .map(AiProjScanSettings.ScanAppType::from)
                .collect(Collectors.toSet());
        result.setBlackBoxEnabled(scanAppTypes.contains(AiProjScanSettings.ScanAppType.BLACKBOX));
        if (Boolean.TRUE.equals(result.getBlackBoxEnabled())) {
            log.trace("Set base project blackbox settings");
            result.setBlackBox(apply(settings, new BlackBoxSettingsBaseModel()));
        }

        return result;
    }

    /**
     * Method converts PT AI API version independent language to PT AI v.4.1.1 API programming language group
     * @param language PT AI API version independent language
     * @return PT AI v.4.1.1 API programming language group
     */
    @NonNull
    protected static ProgrammingLanguageGroup convertLanguageGroup(@NonNull final ScanResult.ScanSettings.Language language) {
        return REVERSE_LANGUAGE_GROUP_MAP.getOrDefault(language, ProgrammingLanguageGroup.NONE);
    }

    @AllArgsConstructor
    @Getter
    protected static class JavaParametersParseResult {
        protected String prefixes;
        protected String other;
    }

    /**
     * @param javaParameters Java CLI parameters that are passed to Java scanning core
     * @return CLI parameters split into two parts: {@link JavaParametersParseResult#prefixes user package prefixes}
     * and {@link JavaParametersParseResult#other remaining part of CLI}
     */
    protected static JavaParametersParseResult parseJavaParameters(final String javaParameters) {
        if (StringUtils.isEmpty(javaParameters)) return null;
        log.trace("Split Java parameters string using 'quote-safe' regular expression");
        String[] parameters = javaParameters.split("(\"[^\"]*\")|(\\S+)");
        if (0 == parameters.length) return null;
        log.trace("Parse Java parameters");
        List<String> commands = new ArrayList<>();
        Map<String, List<String>> arguments = new HashMap<>();
        for (int i = 0 ; i < parameters.length ; i++) {
            log.trace("Iterate through commands");
            if (!parameters[i].startsWith("-")) continue;
            if (parameters.length - 1 == i)
                // If this is last token just add it as command
                commands.add(parameters[i]);
            else if (parameters[i + 1].startsWith("-"))
                // Next token is a command too
                commands.add(parameters[i]);
            else {
                List<String> argumentValues = new ArrayList<>();
                for (int j = i + 1; j < parameters.length; j++)
                    if (!parameters[j].startsWith("-")) argumentValues.add(parameters[j]); else break;
                arguments.put(parameters[i], argumentValues);
            }
        }
        String prefixes = "";
        StringBuilder commandBuilder = new StringBuilder();
        for (String cmd : commands) {
            if ("-upp".equals(cmd) || "--user-package=prefix".equals(cmd))
                if (arguments.containsKey(cmd) && 1 == arguments.get(cmd).size())
                    prefixes = arguments.get(cmd).get(0);
                else {
                    commandBuilder.append(cmd).append(" ");
                    if (arguments.containsKey(cmd))
                        commandBuilder.append(String.join(" ", arguments.get(cmd))).append(" ");
                }
        }
        return new JavaParametersParseResult(prefixes, commandBuilder.toString().trim());
    }

    @SneakyThrows
    protected static JavaSettingsModel apply(
            @NonNull final UnifiedAiProjScanSettings settings,
            @NonNull final JavaSettingsModel model) {
        if (null == settings.getJavaSettings()) return model;
        UnifiedAiProjScanSettings.JavaSettings javaSettings = settings.getJavaSettings();
        // Set isUnpackUserJarFiles
        model.setUnpackUserPackages(javaSettings.isUnpackUserPackages());
        // Set userPackagePrefixes and launchJvmParameters
        log.trace("Try to extract user package prefixes from Java parameters");
        // noinspection ConstantConditions
        do {
            if (StringUtils.isEmpty(javaSettings.getParameters())) break;
            JavaParametersParseResult parseResult = parseJavaParameters(javaSettings.getParameters());
            if (null == parseResult) break;
            model.setUserPackagePrefixes(parseResult.getPrefixes());
            model.setParameters(parseResult.getOther());
        } while (false);
        // Set jdkVersion
        model.setVersion(JAVA_VERSION_MAP.getOrDefault(javaSettings.getJavaVersion(), JavaVersions.v1_8));
        return model;
    }

    @SneakyThrows
    protected static DotNetSettingsModel apply(
            @NonNull final UnifiedAiProjScanSettings settings,
            @NonNull final DotNetSettingsModel model) {
        if (null == settings.getJavaSettings()) return model;
        UnifiedAiProjScanSettings.JavaSettings javaSettings = settings.getJavaSettings();
        // Set projectType
        model.setProjectType(DOTNET_PROJECT_TYPE_MAP.getOrDefault(settings.getProjectType(), DotNetProjectType.NONE);
        // In PT AI v.4.1 solution file is to be defined as "./solution.sln" instead of "solution.sln"
        String solutionFile = settings.getSolutionFile();
        do {
            if (StringUtils.isEmpty(solutionFile)) break;
            solutionFile = solutionFile.trim();
            if (solutionFile.startsWith("./")) break;
            log.trace("Fix solution file name {}", solutionFile);
            solutionFile = "./" + solutionFile;
            log.trace("Fixed solution file name is {}", solutionFile);
        } while (false);
        model.setSolutionFile(solutionFile);
        return model;
    }

    @SneakyThrows
    protected static ComponentsSettingsModel apply(
            @NonNull final UnifiedAiProjScanSettings settings,
            @NonNull final ComponentsSettingsModel model) {
        return model.useCustomYaraRules(settings.isUseCustomYaraRules());
    }

    /**
     * Method sets project settings attributes using AIPROJ-defined ones
     * @param settings
     * @param model
     * @return
     */
    @SneakyThrows
    public static ProjectSettingsModel apply(
            @NonNull final UnifiedAiProjScanSettings settings,
            @NonNull final ProjectSettingsModel model) {
        log.trace("Set base project settings");
        // Set projectSource
        model.setProjectSource(new ProjectSourceModel().sourceType(SourceType.EMPTY));
        // Set projectName
        model.setProjectName(settings.getProjectName());
        // Set programmingLanguageGroup
        model.setProgrammingLanguageGroup(convertLanguageGroup(settings.getProgrammingLanguage()));
        // Set whiteBoxSettings
        model.setWhiteBoxSettings(apply(settings, new WhiteBoxSettingsModel()));
        // Set launchParameters
        model.setLaunchParameters(settings.getCustomParameters());
        //Set useAvailablePublicAndProtectedMethods
        model.setUseAvailablePublicAndProtectedMethods(settings.isUsePublicAnalysisMethod());
        // Set isLoadDependencies
        model.setDownloadDependencies(settings.isDownloadDependencies());
        // Set javaSettings
        model.setJavaSettings(apply(settings.getJavaSettings(), new JavaSettingsModel()));
        // Set .NET
        model.setDotNetSettings(apply(settings, new DotNetSettingsModel()));
        // Set DC
        model.setComponentsSettings(apply(settings, new ComponentsSettingsModel()));
        return model;
    }

    @SneakyThrows
    protected static BlackBoxAuthenticationFullModel apply(
            @NonNull final AiProjScanSettings source,
            @NonNull final BlackBoxAuthenticationFullModel destination) {
        destination.setType(AuthType.NONE);
        log.trace("Check if AIPROJ authentication field is defined");
        if (null == source.getAuthentication()) return destination;
        AiProjScanSettings.Authentication.Item jsonAuth = source.getAuthentication().getItem();
        if (null == jsonAuth || null == jsonAuth.getCredentials()) return destination;
        destination.setType(BLACKBOX_AUTH_TYPE_MAP.getOrDefault(jsonAuth.getCredentials().getType(), AuthType.NONE));

        if (AuthType.FORM == destination.getType()) {
            BlackBoxFormAuthenticationModel form = new BlackBoxFormAuthenticationModel()
                    .formAddress(jsonAuth.getFormUrl())
                    .formXPath(jsonAuth.getFormXPath())
                    .loginKey(null != jsonAuth.getCredentials().getLogin() ? jsonAuth.getCredentials().getLogin().getName() : null)
                    .login(null != jsonAuth.getCredentials().getLogin() ? jsonAuth.getCredentials().getLogin().getValue() : null)
                    .passwordKey(null != jsonAuth.getCredentials().getPassword() ? jsonAuth.getCredentials().getPassword().getName() : null)
                    .password(null != jsonAuth.getCredentials().getPassword() ? jsonAuth.getCredentials().getPassword().getValue() : null)
                    .validationTemplate(jsonAuth.getRegexpOfSuccess());
            destination.setForm(form);
        } else if (AuthType.HTTP == destination.getType()) {
            BlackBoxHttpAuthenticationModel http = new BlackBoxHttpAuthenticationModel()
                    .login(null != jsonAuth.getCredentials().getLogin() ? jsonAuth.getCredentials().getLogin().getValue() : null)
                    .password(null != jsonAuth.getCredentials().getPassword() ? jsonAuth.getCredentials().getPassword().getValue() : null)
                    .validationAddress(jsonAuth.getTestUrl());
            destination.setHttp(http);
        } else if (AuthType.RAWCOOKIE == destination.getType()) {
            BlackBoxRawCookieAuthenticationModel cookie = new BlackBoxRawCookieAuthenticationModel()
                    .cookie(jsonAuth.getCredentials().getCookie())
                    .validationAddress(jsonAuth.getTestUrl())
                    .validationTemplate(jsonAuth.getRegexpOfSuccess());
            destination.setCookie(cookie);
        }
        return destination;
    }

    @SneakyThrows
    protected static BlackBoxProxySettingsModel apply(
            @NonNull final AiProjScanSettings.ProxySettings source,
            @NonNull final BlackBoxProxySettingsModel destination) {
        destination.setIsActive(null != source.getType());
        if (Boolean.FALSE.equals(destination.getIsActive())) return destination;
        destination.setType(BLACKBOX_PROXY_TYPE_MAP.get(source.getType()));
        destination.setHost(source.getHost());
        destination.setPort(source.getPort());
        destination.setLogin(source.getUsername());
        destination.setPassword(source.getPassword());
        return destination;
    }

    @SneakyThrows
    protected static BlackBoxProxySettingsModel apply(final AiProjScanSettings.ProxySettings source) {
        return null == source ? null : apply(source, new BlackBoxProxySettingsModel());
    }

    @SneakyThrows
    public static BlackBoxSettingsModel apply(
            @NonNull final AiProjScanSettings settings,
            @NonNull final BlackBoxSettingsModel model) {
        model.setRunAutocheckAfterScan(settings.getRunAutocheckAfterScan());
        model.setSite(settings.getSite());
        Set<AiProjScanSettings.ScanAppType> scanAppTypes = Arrays.stream(settings.getScanAppType().split("[, ]+"))
                .map(AiProjScanSettings.ScanAppType::from)
                .collect(Collectors.toSet());
        if (!scanAppTypes.contains(AiProjScanSettings.ScanAppType.BLACKBOX)) return model;
        model.setIsActive(true);
        model.setLevel(BLACKBOX_SCAN_LEVEL_MAP.get(settings.getBlackBoxScanLevel()));
        model.setScanScope(BLACKBOX_SCAN_SCOPE_MAP.get(settings.getBlackBoxScanScope()));
        if (CollectionUtils.isNotEmpty(settings.getCustomHeaders())) {
            log.trace("Set additional HTTP headers");
            Map<String, String> headers = new HashMap<>();
            for (List<String> header : settings.getCustomHeaders()) {
                if (2 != header.size()) continue;
                headers.put(header.get(0), header.get(1));
            }
            model.setAdditionalHttpHeaders(headers);
        }
        model.setAuthentication(apply(settings, new BlackBoxAuthenticationFullModel()));
        model.setProxySettings(null == settings.getProxySettings() ? null : apply(settings.getProxySettings()));
        return model;
    }

    @SneakyThrows
    protected static BlackBoxSettingsBaseModel apply(
            @NonNull final AiProjScanSettings settings,
            @NonNull final BlackBoxSettingsBaseModel model) {
        model.setRunAutocheckAfterScan(settings.getRunAutocheckAfterScan());
        model.setSite(settings.getSite());
        Set<AiProjScanSettings.ScanAppType> scanAppTypes = Arrays.stream(settings.getScanAppType().split("[, ]+"))
                .map(AiProjScanSettings.ScanAppType::from)
                .collect(Collectors.toSet());
        if (!scanAppTypes.contains(AiProjScanSettings.ScanAppType.BLACKBOX)) return model;
        model.setLevel(BLACKBOX_SCAN_LEVEL_MAP.get(settings.getBlackBoxScanLevel()));
        model.setScanScope(BLACKBOX_SCAN_SCOPE_MAP.get(settings.getBlackBoxScanScope()));
        if (CollectionUtils.isNotEmpty(settings.getCustomHeaders())) {
            log.trace("Set additional HTTP headers");
            Map<String, String> headers = new HashMap<>();
            for (List<String> header : settings.getCustomHeaders()) {
                if (2 != header.size()) continue;
                headers.put(header.get(0), header.get(1));
            }
            model.setAdditionalHttpHeaders(headers);
        }
        model.setAuthentication(apply(settings, new BlackBoxAuthenticationFullModel()));
        model.setProxySettings(null == settings.getProxySettings() ? null : apply(settings.getProxySettings()));
        return model;
    }

    @SneakyThrows
    public static SecurityPoliciesModel apply(
            final Policy[] policy,
            @NonNull final SecurityPoliciesModel model) {
        model.setCheckSecurityPoliciesAccordance(null != policy && 0 != policy.length);
        model.setSecurityPolicies(Boolean.TRUE.equals(model.getCheckSecurityPoliciesAccordance()) ? JsonPolicyHelper.serialize(policy) : "");
        return model;
    }

    public static AiProjScanSettings verify(String json) throws GenericException {
        return call(() -> {
            ObjectMapper mapper = createObjectMapper();
            AiProjScanSettings res = mapper.readValue(json, AiProjScanSettings.class);
            if (StringUtils.isEmpty(res.getProjectName()))
                throw new IllegalArgumentException("ProjectName field is not defined or empty");
            if (null == res.getProgrammingLanguage())
                throw new IllegalArgumentException("ProgrammingLanguage field is not defined or empty");
            return res.fix();
        }, "JSON settings parse failed");
    }

    protected static String serialize(AiProjScanSettings settings) throws GenericException {
        return call(
                () -> BaseJsonHelper.serialize(settings.fix()),
                "JSON settings serialization failed");
    }

    /**
     * @param settingsJson JSON-defined AST settings
     * @return Minimized JSON-defined AST settings, i.e. without comments, formatting etc.
     * @throws GenericException
     */
    protected static String minimize(@NonNull String settingsJson) throws GenericException {
        AiProjScanSettings settings = verify(settingsJson);
        return serialize(settings);
    }
}
