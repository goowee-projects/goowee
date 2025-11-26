package custom

import goowee.elements.Page
import goowee.elements.components.Header
import goowee.elements.contents.ContentBlank

class CustomPage extends Page {

    Header header
    ContentBlank content

    CustomPage(Map args) {
        super(args)

        viewPath = '/custom/'

        header = createComponent(Header)
        content = createComponent(ContentBlank, 'content')
    }

}
