package cl.sanaesencia.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class TelefonoVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.take(8)
        val prefix = "+56 9 "
        val sb = StringBuilder(prefix)
        for (i in clean.indices) {
            if (i == 4) sb.append(' ')
            sb.append(clean[i])
        }
        val transformed = sb.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return prefix.length
                val clamped = offset.coerceAtMost(clean.length)
                var spaces = 0
                if (clamped > 4) spaces = 1
                return prefix.length + clamped + spaces
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= prefix.length) return 0
                val transformedOffset = offset - prefix.length
                val digits = if (transformedOffset > 4) transformedOffset - 1 else transformedOffset
                return digits.coerceAtMost(clean.length)
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}
