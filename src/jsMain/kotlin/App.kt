import kotlinx.css.CSSBuilder
import kotlinx.css.backgroundColor
import kotlinx.css.hsl
import kotlin.browser.document

fun main() {
    document.head!!.insertAdjacentHTML("beforeend", "<style>$styles</style>")
}
var styles = CSSBuilder().apply {
    "html" {
        backgroundColor = hsl(220, 22, 24)
    }
    "body" {
        backgroundColor = hsl(224, 22, 10)
    }
}
