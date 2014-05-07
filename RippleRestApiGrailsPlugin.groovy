class RippleRestApiGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def title = "Ripple Rest Api" // Headline display name of the plugin
    def author = "Simeon Gbolo"
    def authorEmail = "simdevgb@gmail.com"
    def description = '''
A Grails implementation for the Ripple REST API (https://github.com/ripple/ripple-rest/blob/develop/docs/api-reference.md).
'''

    // URL to the plugin's documentation this will be my git repo(will have documentation)
    def documentation = "https://github.com/simeongbolo/Ripple-Rest-Plugin"


}
