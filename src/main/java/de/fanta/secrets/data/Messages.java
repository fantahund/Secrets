package de.fanta.secrets.data;

public class Messages {

    private String prefix;
    private String deleteSecretfromPlayersError;
    private String deleteSecretfromPlayersSuccessful;
    private String loadDatabase;
    private String deleteSecretError;
    private String deleteSecretSuccessful;
    private String secretNotExist;
    private String setDisplayItemError;
    private String setDisplayItemSuccessful;
    private String setDisplayItemNoItem;
    private String guiTitle;
    private String guiScrollUp;
    private String guiScrollDown;
    private String guiClose;
    private String guiSecretsFoundName;
    private String guiSecretsFoundLore;
    private String lobbyItemName;
    private String secretFound;
    private String secretAlwaysFound;
    private String secretFoundTitle;
    private String secretFoundSubTitle;
    private String secretCreateError;
    private String secretCreateSuccessful;
    private String secretCreateWrongSecretLineError;
    private String signLine1;
    private String signLine2;
    private String signLine3;
    private String signLine4;
    private String economyReward;
    private String secretExistsCreatedNewSign;
    private String giveDisplayItemSuccessful;
    private String giveDisplayItemError;

    public Messages(LanguageManager languageManager) {
        loadMessages(languageManager);
    }

    public void loadMessages(LanguageManager languageManager) {
        prefix = languageManager.getMessage("prefix");
        deleteSecretfromPlayersError = languageManager.getMessage("deleteSecretfromPlayersError");
        deleteSecretfromPlayersSuccessful = languageManager.getMessage("deleteSecretfromPlayersSuccessful");
        loadDatabase = languageManager.getMessage("loadDatabase");
        deleteSecretError = languageManager.getMessage("deleteSecretError");
        deleteSecretSuccessful = languageManager.getMessage("deleteSecretSuccessful");
        secretNotExist = languageManager.getMessage("secretNotExist");
        setDisplayItemError = languageManager.getMessage("setDisplayItemError");
        setDisplayItemSuccessful = languageManager.getMessage("setDisplayItemSuccessful");
        setDisplayItemNoItem = languageManager.getMessage("setDisplayItemNoItem");
        guiTitle = languageManager.getMessage("guiTitle");
        guiScrollUp = languageManager.getMessage("guiScrollUp");
        guiScrollDown = languageManager.getMessage("guiScrollDown");
        guiClose = languageManager.getMessage("guiClose");
        guiSecretsFoundName = languageManager.getMessage("guiSecretsFoundName");
        guiSecretsFoundLore = languageManager.getMessage("guiSecretsFoundLore");
        lobbyItemName = languageManager.getMessage("lobbyItemName");
        secretFound = languageManager.getMessage("secretFound");
        secretAlwaysFound = languageManager.getMessage("secretAlwaysFound");
        secretFoundTitle = languageManager.getMessage("secretFoundTitle");
        secretFoundSubTitle = languageManager.getMessage("secretFoundSubTitle");
        secretCreateError = languageManager.getMessage("secretCreateError");
        secretCreateSuccessful = languageManager.getMessage("secretCreateSuccessful");
        secretCreateWrongSecretLineError = languageManager.getMessage("secretCreateWrongSecretLineError");
        signLine1 = languageManager.getMessage("signLine1");
        signLine2 = languageManager.getMessage("signLine2");
        signLine3 = languageManager.getMessage("signLine3");
        signLine4 = languageManager.getMessage("signLine4");
        economyReward = languageManager.getMessage("economyReward");
        secretExistsCreatedNewSign = languageManager.getMessage("secretExistsCreatedNewSign");
        giveDisplayItemSuccessful = languageManager.getMessage("giveDisplayItemSuccessful");
        giveDisplayItemError = languageManager.getMessage("giveDisplayItemError");
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDeleteSecretfromPlayersError(String secretName) {
        return deleteSecretfromPlayersError.replace("%secret%", secretName);
    }

    public String getDeleteSecretfromPlayersSuccessful(String secretName) {
        return deleteSecretfromPlayersSuccessful.replace("%secret%", secretName);
    }

    public String getReloadDatabase() {
        return loadDatabase;
    }

    public String getDeleteSecretError(String secretName) {
        return deleteSecretError.replace("%secret%", secretName);
    }

    public String getDeleteSecretSuccessful(String secretName) {
        return deleteSecretSuccessful.replace("%secret%", secretName);
    }

    public String getSecretNotExist(String secretName) {
        return secretNotExist.replace("%secret%", secretName);
    }

    public String getSetDisplayItemError(String secretName) {
        return setDisplayItemError.replace("%secret%", secretName);
    }

    public String getSetDisplayItemSuccessful(String secretName) {
        return setDisplayItemSuccessful.replace("%secret%", secretName);
    }

    public String getSetDisplayItemNoItem() {
        return setDisplayItemNoItem;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public String getGuiScrollUp() {
        return guiScrollUp;
    }

    public String getGuiScrollDown() {
        return guiScrollDown;
    }

    public String getGuiClose() {
        return guiClose;
    }

    public String getGuiSecretsFoundName() {
        return guiSecretsFoundName;
    }

    public String getGuiSecretsFoundLore(int count, int found) {
        return guiSecretsFoundLore.replace("%secret_size%", String.valueOf(count)).replace("%secrets_found%", String.valueOf(found));
    }

    public String getLobbyItemName() {
        return lobbyItemName;
    }

    public String getSecretFound(String secretName) {
        return secretFound.replace("%secret%", secretName);
    }

    public String getSecretAlwaysFound(String secretName) {
        return secretAlwaysFound.replace("%secret%", secretName);
    }

    public String getSecretFoundTitle(String secretName) {
        return secretFoundTitle.replace("%secret%", secretName);
    }

    public String getSecretFoundSubTitle(String secretName) {
        return secretFoundSubTitle.replace("%secret%", secretName);
    }

    public String getSecretCreateError(String secretName) {
        return secretCreateError.replace("%secret%", secretName);
    }

    public String getSecretCreateSuccessful(String secretName) {
        return secretCreateSuccessful.replace("%secret%", secretName);
    }

    public String getSecretCreateWrongSecretLineError() {
        return secretCreateWrongSecretLineError;
    }

    public String getSignLine1(String secretName) {
        return signLine1.replace("%secret%", secretName);
    }

    public String getSignLine2(String secretName) {
        return signLine2.replace("%secret%", secretName);
    }

    public String getSignLine3(String secretName) {
        return signLine3.replace("%secret%", secretName);
    }

    public String getSignLine4(String secretName) {
        return signLine4.replace("%secret%", secretName);
    }

    public String getEconomyReward(double money, String currency) {
        return economyReward.replace("%money%", String.valueOf(money)).replace("%currency%", currency);
    }

    public String getSecretExistsCreatedNewSign(String secretName) {
        return secretExistsCreatedNewSign.replace("%secret%", secretName);
    }

    public String getGiveDisplayItemSuccessful(String secretName) {
        return giveDisplayItemSuccessful.replace("%secret%", secretName);
    }

    public String getGiveDisplayItemError(String secretName) {
        return giveDisplayItemError.replace("%secret%", secretName);
    }
}
