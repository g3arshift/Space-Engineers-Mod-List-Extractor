import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModList {
    private final Pattern STEAM_WORKSHOP_ID_REGEX_PATTERN = Pattern.compile("([0-9])\\d*");
    private final String[] modText;

    public ModList(File sandboxFile) throws IOException {
        String sandboxFileContent = Files.readString(sandboxFile.toPath());
        modText = StringUtils.substringAfter(sandboxFileContent, "<Mods>").split(System.lineSeparator());
    }

    public String getModListUrls() {
        List<String> modListUrls = new ArrayList<>();
        //Grab the first mod ID field, then each after.
        for (int i = 3; i < modText.length - 4; i += 5) {
            modListUrls.add(createModUrl(modText[i]) + "\n");
        }

        return String.join("", modListUrls);
    }


    private String createModUrl(String modIdText) {
        String STEAM_WORKSHOP_URL = "https://steamcommunity.com/sharedfiles/filedetails/?id=";
        return STEAM_WORKSHOP_URL + STEAM_WORKSHOP_ID_REGEX_PATTERN.matcher(modIdText).results().map(MatchResult::group).collect(Collectors.joining(""));
    }
}
