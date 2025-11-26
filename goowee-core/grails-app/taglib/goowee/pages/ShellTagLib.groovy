package goowee.pages

import goowee.core.ApplicationService
import goowee.core.WebRequestAware
import goowee.elements.pages.ShellService

/**
 * @author Gianluca Sartori
 */
class ShellTagLib implements WebRequestAware {

    ApplicationService applicationService
    ShellService shellService

    static namespace = "shell"
    String tagsTemplatesPath = '/goowee/components/shell/'

    def title = {
        out << shellService.shell.content.title
    }

    def displayFlagCode = {
        out << applicationService.getFlagCode(currentLanguage)
    }

//    def displayUserNotifications = {
//        if (applicationService.hasPlugin('notifications')) {
//            out << g.render(template: tagsTemplatesPath + "displayTopBarNotifications")
//        }
//    }
//
//    def displayUserMenuNotifications = {
//        if (applicationService.hasPlugin('notifications')) {
//            out << g.render(template: tagsTemplatesPath + "displayUserMenuNotifications")
//            out << g.render(template: tagsTemplatesPath + "displayUserMenuSeparator")
//        }
//    }

    def displayUserMenuExtensions = { attrs ->
        def menu = shellService.shell.config.features.user.items
        if (menu.size() > 0) {
            out << g.render(template: tagsTemplatesPath + "displayUserMenuSeparator")
        }
        for (candy in menu) {
            out << g.render(template: tagsTemplatesPath + "displayUserMenuExtensions", model: [candy: candy])
        }
    }
}
