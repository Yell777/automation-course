package pages;

import com.microsoft.playwright.*;

public class BasePage {

    public BasePage(Page page) {
        this.page = page;
    }

    protected Page page;


}
