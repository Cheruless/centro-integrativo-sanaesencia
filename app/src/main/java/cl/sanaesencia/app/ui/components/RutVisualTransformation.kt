package cl.sanaesencia.app.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formatea el RUT numérico (run) con puntos mientras el usuario escribe.
 * "20781681" se muestra como "20.781.681". Solo recibe dígitos.
 */
class RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val formatted = formatRun(raw)
        return TransformedText(AnnotatedString(formatted), RutOffsetMapping(raw))
    }

    private class RutOffsetMapping(private val raw: String) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            var transformed = 0
            var digitCount = 0
            for (i in 0 until minOf(offset, raw.length)) {
                digitCount++
                transformed++
                if ((raw.length - digitCount) % 3 == 0 && digitCount < raw.length) {
                    transformed++
                }
            }
            return transformed
        }

        override fun transformedToOriginal(offset: Int): Int {
            var digitCount = 0
            val formatted = formatRun(raw)
            for (i in 0 until minOf(offset, formatted.length)) {
                if (formatted[i].isDigit()) digitCount++
            }
            return digitCount
        }
    }
}

private fun formatRun(run: String): String {
    if (run.isEmpty()) return ""
    val reversed = run.reversed()
    return reversed.chunked(3).joinToString(".").reversed()
}
