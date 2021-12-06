package com.hc.wanandroid.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import coil.compose.rememberImagePainter
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

private const val TAG_URL = "url"

private const val ID_IMAGE = "image"

data class ResolvedLink(
    val expanded: String?,
    val skip: Boolean = false,
    val display: String? = null,
    val clickable: Boolean = true,
)

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    htmlText: String,
    maxLines: Int = Int.MAX_VALUE,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)),
    linkStyle: TextStyle = textStyle.copy(MaterialTheme.colors.primary),
    linkResolver: (href: String) -> ResolvedLink = { ResolvedLink(it) },
) {

    RenderContent(
        modifier = modifier,
        htmlText = htmlText,
        linkResolver = linkResolver,
        maxLines = maxLines,
        textStyle = textStyle,
        linkStyle = linkStyle,
        onLinkClicked = {

        },
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
    )

}


@Composable
private fun RenderContent(
    modifier: Modifier = Modifier,
    htmlText: String,
    textStyle: TextStyle,
    linkStyle: TextStyle,
    linkResolver: (href: String) -> ResolvedLink = { ResolvedLink(it) },
    onLinkClicked: (String) -> Unit = {},
    maxLines: Int = Int.MAX_VALUE,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
) {
    val value = renderContentAnnotatedString(
        htmlText = htmlText,
        linkResolver = linkResolver,
        textStyle = textStyle,
        linkStyle = linkStyle
    )
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    if (value.text.isNotEmpty() && value.text.isNotBlank()) {
        Text(
            modifier = modifier.pointerInput(Unit) {
                forEachGesture {
                    coroutineScope {
                        val change = awaitPointerEventScope {
                            awaitFirstDown()
                        }
                        val annotation =
                            layoutResult.value?.getOffsetForPosition(change.position)?.let {
                                value.getStringAnnotations(start = it, end = it)
                                    .firstOrNull()
                            }
                        if (annotation != null) {
                            change.consumeDownChange()
                            val up = awaitPointerEventScope {
                                waitForUpOrCancellation()?.also { it.consumeDownChange() }
                            }
                            if (up != null) {
                                onLinkClicked(annotation.item)
                            }
                        }
                    }
                }
            },
            maxLines = maxLines,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            text = value,
            onTextLayout = {
                layoutResult.value = it
            },
            inlineContent = mapOf(
                ID_IMAGE to InlineTextContent(
                    Placeholder(
                        width = LocalTextStyle.current.fontSize,
                        height = LocalTextStyle.current.fontSize,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) { target ->
                    Image(
                        rememberImagePainter(data = target), target
                    )
                }
            ),
        )
    }
}


@Composable
fun renderContentAnnotatedString(
    htmlText: String,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)),
    linkStyle: TextStyle = textStyle.copy(MaterialTheme.colors.primary),
    linkResolver: (href: String) -> ResolvedLink,
): AnnotatedString {
    val styleData = remember(textStyle, linkStyle) {
        StyleData(
            textStyle = textStyle,
            linkStyle = linkStyle,
        )
    }
    val renderContext = remember(linkResolver) {
        RenderContext(linkResolver = linkResolver)
    }
    return remember(
        htmlText,
        styleData,
    ) {
        val document = Jsoup.parse(htmlText.replace("\n", "<br>"))
        buildAnnotatedString {
            document.body().childNodes().forEach {
                renderNode(it, renderContext, styleData)
            }
        }
    }
}

private data class RenderContext(
    val linkResolver: (href: String) -> ResolvedLink,
)

data class StyleData(
    val textStyle: TextStyle,
    val linkStyle: TextStyle,
)

private fun AnnotatedString.Builder.renderNode(
    node: Node,
    context: RenderContext,
    styleData: StyleData
) {
    when (node) {
        is Element -> {
            this.renderElement(node, context = context, styleData = styleData)
        }
        is TextNode -> {
            renderText(node.wholeText, styleData.textStyle)
        }
    }
}

private fun AnnotatedString.Builder.renderText(text: String, textStyle: TextStyle) {
    pushStyle(
        textStyle.toSpanStyle()
    )
    append(text)
    pop()
}

private fun AnnotatedString.Builder.renderElement(
    element: Element,
    context: RenderContext,
    styleData: StyleData
) {
    if (skipElement(element = element)) {
        return
    }
    when (element.normalName()) {
        "a" -> {
            renderLink(element, context, styleData)
        }
        "br" -> {
            renderText("\n", styleData.textStyle)
        }
        "span", "p" -> {
            element.childNodes().forEach {
                renderNode(node = it, context = context, styleData = styleData)
            }
        }
        "emoji" -> {
            renderEmoji(element)
        }
    }
}

private fun skipElement(element: Element): Boolean {
    return element.hasClass("invisible")
}

private fun AnnotatedString.Builder.renderEmoji(
    element: Element,
) {
    val target = element.attr("target")
    appendInlineContent(ID_IMAGE, target)
}

private fun AnnotatedString.Builder.renderLink(
    element: Element,
    context: RenderContext,
    styleData: StyleData
) {
    val href = element.attr("href")
    val resolvedLink = context.linkResolver.invoke(href)
    when {
        resolvedLink.expanded != null -> {
            if (resolvedLink.clickable) {
                pushStringAnnotation(TAG_URL, resolvedLink.expanded)
                renderText(resolvedLink.display ?: resolvedLink.expanded, styleData.linkStyle)
                pop()
            } else {
                renderText(resolvedLink.display ?: resolvedLink.expanded, styleData.textStyle)
            }
        }
        resolvedLink.skip -> {
        }
        else -> {
            if (resolvedLink.clickable) {
                pushStringAnnotation(TAG_URL, href)
                element.childNodes().forEach {
                    renderNode(
                        node = it,
                        context = context,
                        styleData = styleData.copy(textStyle = styleData.linkStyle)
                    )
                }
                pop()
            } else {
                element.childNodes().forEach {
                    renderNode(
                        node = it,
                        context = context,
                        styleData = styleData.copy(textStyle = styleData.textStyle)
                    )
                }
            }
        }
    }
}
