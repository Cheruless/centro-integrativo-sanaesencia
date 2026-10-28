package cl.sanaesencia.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.take(9).uppercase()
        val sb = StringBuilder()
        for (i in clean.indices) {
            sb.append(clean[i])
            val remaining = clean.length - 1 - i
            if (remaining == 1) sb.append('-')
            else if (remaining > 1 && (remaining - 1) % 3 == 0) sb.append('.')
        }
        val transformed = sb.toString()
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var o = 0; var t = 0
                while (o < offset.coerceAtMost(clean.length) && t < transformed.length) {
                    if (transformed[t] != '.' && transformed[t] != '-') o++
                    t++
                }
                return t
            }
            override fun transformedToOriginal(offset: Int): Int {
                var o = 0
                for (i in 0 until offset.coerceAtMost(transformed.length)) {
                    if (transformed[i] != '.' && transformed[i] != '-') o++
                }
                return o.coerceAtMost(clean.length)
            }
        }
        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}
