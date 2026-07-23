/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package goowee.elements.components

import goowee.core.LinkDefinition
import goowee.elements.ComponentEvent
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class Link extends Label {

    /** The link */
    LinkDefinition linkDefinition

    /** The event that triggers the link. Defaults to 'click' */
    String triggerEvent

    /**
     * Creates a new Link component.
     *
     * @param args a map of configuration options. Supports all {@link Label} arguments plus:
     *             {@code linkDefinition}, {@code tag}, {@code html}, {@code triggerEvent}, {@code onClick}
     */
    Link(Map args = [:]) {
        super(args)

        linkDefinition = args.linkDefinition
                ? args.linkDefinition as LinkDefinition
                : new LinkDefinition(args)

        tag = args.tag == null ? false : args.tag

        html = args.html
        containerSpecs.label = ''
        containerSpecs.help = ''

        triggerEvent = args.triggerEvent ?: 'click'
        onTrigger(args.onClick as String)
    }

    /**
     * Registers the link's trigger event with the underlying event system.
     * Merges the link definition properties with the resolved action, info message,
     * and confirm message.
     *
     * @param action an optional action override; if {@code null}, the action from
     *               {@link #linkDefinition} is used
     */
    void onTrigger(String action = null) {
        on(linkDefinition.properties + [
                event         : triggerEvent,
                action        : action ?: linkDefinition.action,
                infoMessage   : message(linkDefinition.infoMessage, linkDefinition.infoMessageArgs),
                confirmMessage: message(linkDefinition.confirmMessage, linkDefinition.confirmMessageArgs),
        ])
    }

    /**
     * Returns a JSON string of this component's properties, merged with any additional
     * properties provided by subclasses or callers.
     *
     * @param properties additional properties to merge into the JSON output
     * @return JSON representation of this component's properties
     */
    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                loading: loading,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    /**
     * Returns a human-readable representation of this link, including the resolved path
     * and any query parameters.
     *
     * @return a string in the form {@code "Link: /namespace/controller/action?param=value"}
     */
    String toString() {
        String ns = linkDefinition.namespace ?: ''
        String path = "${ns}/${linkDefinition.controller}/${linkDefinition.action}"
        String queryParams = linkDefinition.params.collect { "${it.key}=${it.value}" }.join('&')
        String queryString = queryParams ? "?${queryParams}" : ''
        return "Link: ${path}${queryString}"
    }

    /**
     * Returns a development-time URL for this link, useful for previewing navigation
     * targets without a full server context.
     * Returns the explicit {@code url} if set, otherwise falls back to
     * {@code /controller/action}, or {@code null} if neither is available.
     *
     * @return the dev URL string, or {@code null}
     */
    String getDevUrl() {
        if (url) return url
        if (controller && this.action) return "/${controller}/${this.action}"
        return null
    }

    /**
     * Sets a new {@link LinkDefinition} and re-registers the trigger event accordingly.
     *
     * @param link the new link definition to apply
     */
    void setLinkDefinition(LinkDefinition link) {
        linkDefinition = link
        onTrigger()
    }

    /**
     * Returns the HTML {@code target} attribute of the link (e.g. {@code "_blank"}).
     *
     * @return the target value, or {@code null} if not set
     */
    String getTarget() {
        return linkDefinition.target
    }

    /**
     * Sets the HTML {@code target} attribute of the link.
     *
     * @param value the target value (e.g. {@code "_blank"})
     */
    void setTarget(String value) {
        linkDefinition.target = value
    }

    /**
     * Returns the Grails URL namespace segment of the link.
     *
     * @return the namespace, or {@code null} if not set
     */
    String getNamespace() {
        return linkDefinition.namespace
    }

    /**
     * Sets the Grails URL namespace and re-registers the trigger event.
     *
     * @param value the namespace to set
     */
    void setNamespace(String value) {
        linkDefinition.namespace = value
        onTrigger()
    }

    /**
     * Returns the Grails controller name targeted by this link.
     *
     * @return the controller name, or {@code null} if not set
     */
    String getController() {
        return linkDefinition.controller
    }

    /**
     * Sets the Grails controller name and re-registers the trigger event.
     *
     * @param value the controller name to set
     */
    void setController(String value) {
        linkDefinition.controller = value
        onTrigger()
    }

    /**
     * Returns the Grails controller action targeted by this link.
     *
     * @return the action name, or {@code null} if not set
     */
    String getAction() {
        return linkDefinition.action
    }

    /**
     * Sets the Grails controller action and re-registers the trigger event.
     *
     * @param value the action name to set
     */
    void setAction(String value) {
        linkDefinition.action = value
        onTrigger()
    }

    /**
     * Returns the query parameters map appended to the link URL.
     *
     * @return a map of parameter key-value pairs, never {@code null}
     */
    Map getParams() {
        return linkDefinition.params
    }

    /**
     * Sets the query parameters map and re-registers the trigger event.
     *
     * @param value the map of query parameters to set
     */
    void setParams(Map value) {
        linkDefinition.params = value
        onTrigger()
    }

    /**
     * Returns the URL fragment (anchor) of the link.
     *
     * @return the fragment string, or {@code null} if not set
     */
    String getFragment() {
        return linkDefinition.fragment
    }

    /**
     * Sets the URL fragment (anchor) and re-registers the trigger event.
     *
     * @param value the fragment string to set
     */
    void setFragment(String value) {
        linkDefinition.fragment = value
        onTrigger()
    }

    /**
     * Returns the explicit path of the link, used when not relying on controller/action routing.
     *
     * @return the path string, or {@code null} if not set
     */
    String getPath() {
        return linkDefinition.path
    }

    /**
     * Sets the explicit path and re-registers the trigger event.
     *
     * @param value the path string to set
     */
    void setPath(String value) {
        linkDefinition.path = value
        onTrigger()
    }

    /**
     * Returns the absolute URL of the link, bypassing controller/action resolution.
     *
     * @return the URL string, or {@code null} if not set
     */
    String getUrl() {
        return linkDefinition.url
    }

    /**
     * Sets the absolute URL and re-registers the trigger event.
     *
     * @param value the URL string to set
     */
    void setUrl(String value) {
        linkDefinition.url = value
        onTrigger()
    }

    /**
     * Returns the list of form IDs that should be submitted when this link is triggered.
     *
     * @return a list of form ID strings, or {@code null} if not set
     */
    List<String> getSubmit() {
        return linkDefinition.submit
    }

    /**
     * Convenience setter that wraps a single form ID in a list before delegating
     * to {@link #setSubmit(List)}.
     *
     * @param value a single form ID string
     */
    void setSubmit(String value) {
        setSubmit([value])
    }

    /**
     * Sets the list of form IDs to submit and re-registers the trigger event.
     *
     * @param value the list of form ID strings to set
     */
    void setSubmit(List<String> value) {
        linkDefinition.submit = value
        onTrigger()
    }

    /**
     * Returns whether the link navigates directly to the URL without going through
     * the Grails URL mapping system.
     *
     * @return {@code true} if direct navigation is enabled, {@code false} otherwise
     */
    Boolean getDirect() {
        return linkDefinition.direct
    }

    /**
     * Sets whether the link should navigate directly, bypassing URL mapping.
     *
     * @param value {@code true} to enable direct navigation
     */
    void setDirect(Boolean value) {
        linkDefinition.direct = value
    }

    /**
     * Returns whether the link response should be rendered in a modal dialog.
     *
     * @return {@code true} if the response opens in a modal, {@code false} otherwise
     */
    Boolean getModal() {
        return linkDefinition.renderProperties.modal
    }

    /**
     * Sets whether the link response should open in a modal dialog and re-registers
     * the trigger event.
     *
     * @param value {@code true} to render the response in a modal
     */
    void setModal(Boolean value) {
        linkDefinition.renderProperties.modal = value
        onTrigger()
    }

    /**
     * Returns whether the link should be rendered in a small visual style.
     *
     * @return {@code true} if the small style is enabled, {@code false} otherwise
     */
    Boolean getSmall() {
        return linkDefinition.renderProperties.small
    }

    /**
     * Sets whether the link should use the small visual style.
     *
     * @param value {@code true} to enable the small style
     */
    void setSmall(Boolean value) {
        linkDefinition.renderProperties.small = value
    }

    /**
     * Returns whether the link should be rendered in a large visual style.
     *
     * @return {@code true} if the large style is enabled, {@code false} otherwise
     */
    Boolean getLarge() {
        return linkDefinition.renderProperties.large
    }

    /**
     * Sets whether the link should use the large visual style and re-registers
     * the trigger event.
     *
     * @param value {@code true} to enable the large style
     */
    void setLarge(Boolean value) {
        linkDefinition.renderProperties.large = value
        onTrigger()
    }

    /**
     * Returns the animation style applied to the link during loading or transitions.
     *
     * @return the animation identifier string, or {@code null} if not set
     */
    String getAnimate() {
        return linkDefinition.renderProperties.animate
    }

    /**
     * Sets the animation style and re-registers the trigger event.
     *
     * @param value the animation identifier string to set
     */
    void setAnimate(String value) {
        linkDefinition.renderProperties.animate = value
        onTrigger()
    }

    /**
     * Returns whether a close button should be shown alongside this link
     * (typically inside a modal).
     *
     * @return {@code true} if the close button is enabled, {@code false} otherwise
     */
    Boolean getCloseButton() {
        return linkDefinition.renderProperties.closeButton
    }

    /**
     * Sets whether a close button should be shown and re-registers the trigger event.
     *
     * @param value {@code true} to display the close button
     */
    void setCloseButton(Boolean value) {
        linkDefinition.renderProperties.closeButton = value
        onTrigger()
    }

    /**
     * Returns the scroll behaviour applied after the link response is rendered.
     *
     * @return the scroll target or mode string, or {@code null} if not set
     */
    String getScroll() {
        return linkDefinition.renderProperties.scroll
    }

    /**
     * Sets the scroll behaviour and re-registers the trigger event.
     *
     * @param value the scroll target or mode string to set
     */
    void setScroll(String value) {
        linkDefinition.renderProperties.scroll = value
        onTrigger()
    }

    /**
     * Returns whether the link should open in a new browser tab or window.
     *
     * @return {@code true} if the link opens in a new tab/window, {@code false} otherwise
     */
    Boolean getTargetNew() {
        return linkDefinition.targetNew
    }

    /**
     * Sets whether the link should open in a new browser tab or window and
     * re-registers the trigger event.
     *
     * @param value {@code true} to open in a new tab/window
     */
    void setTargetNew(Boolean value) {
        linkDefinition.targetNew = value
        onTrigger()
    }

    /**
     * Returns whether the link should display a loading indicator while the request
     * is in progress.
     *
     * @return {@code true} if the loading indicator is enabled, {@code false} otherwise
     */
    Boolean getLoading() {
        return linkDefinition.loading
    }

    /**
     * Sets whether the loading indicator should be shown and re-registers the trigger event.
     *
     * @param value {@code true} to enable the loading indicator
     */
    void setLoading(Boolean value) {
        linkDefinition.loading = value
        onTrigger()
    }

    /**
     * Returns the i18n message code for the informational toast/notification shown
     * after the link action completes.
     *
     * @return the message code, or {@code null} if not set
     */
    String getInfoMessage() {
        return linkDefinition.infoMessage
    }

    /**
     * Sets the i18n message code for the informational notification and re-registers
     * the trigger event.
     *
     * @param value the message code to set
     */
    void setInfoMessage(String value) {
        linkDefinition.infoMessage = value
        onTrigger()
    }

    /**
     * Sets the arguments used to interpolate the info message and re-registers
     * the trigger event.
     *
     * @param value the list of arguments for the info message
     */
    void setInfoMessageArgs(List value) {
        linkDefinition.infoMessageArgs = value
        onTrigger()
    }

    /**
     * Returns the i18n message code for the confirmation dialog shown before the
     * link action is executed.
     *
     * @return the message code, or {@code null} if not set
     */
    String getConfirmMessage() {
        return linkDefinition.confirmMessage
    }

    /**
     * Sets the i18n message code for the confirmation dialog and re-registers
     * the trigger event.
     *
     * @param value the message code to set
     */
    void setConfirmMessage(String value) {
        linkDefinition.confirmMessage = value
        onTrigger()
    }

    /**
     * Sets the arguments used to interpolate the confirm message and re-registers
     * the trigger event.
     *
     * @param value the list of arguments for the confirm message
     */
    void setConfirmMessageArgs(List value) {
        linkDefinition.confirmMessageArgs = value
        onTrigger()
    }

    /**
     * Sets the event to fire when the user confirms the confirmation dialog, then
     * re-registers the trigger event.
     *
     * @param value the {@link ComponentEvent} to execute on confirmation
     */
    void setConfirmMessageOnConfirm(ComponentEvent value) {
        linkDefinition.confirmMessageOnConfirm = value.asMap()
        onTrigger()
    }
}
