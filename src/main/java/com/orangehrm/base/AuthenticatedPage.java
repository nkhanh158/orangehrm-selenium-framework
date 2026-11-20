package com.orangehrm.base;

import com.orangehrm.components.HeaderComponent;
import com.orangehrm.components.SidebarComponent;
import com.orangehrm.components.ToastComponent;
import org.openqa.selenium.WebDriver;

/**
 * Page inside the logged-in layout, which always has a header, a side menu and toast notifications.
 */
public abstract class AuthenticatedPage extends BasePage {

    private final HeaderComponent header;
    private final SidebarComponent sidebar;
    private final ToastComponent toast;

    protected AuthenticatedPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
        this.sidebar = new SidebarComponent(driver);
        this.toast = new ToastComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public SidebarComponent sidebar() {
        return sidebar;
    }

    public ToastComponent toast() {
        return toast;
    }
}
