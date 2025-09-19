package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class DynamicControlsPage {
    private final Page page;

    public DynamicControlsPage(Page page) {
        this.page = page;
    }

    public void clickRemoveButton() {
        page.locator("button:has-text('Remove')").click();
    }

    public boolean isCheckboxVisible() {
        return page.locator("#checkbox").isVisible();
    }

    public void waitCheckBovBeHidden() {
        page.waitForSelector("#checkbox", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
    }
}
