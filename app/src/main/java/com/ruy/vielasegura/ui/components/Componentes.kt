package com.ruy.vielasegura.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruy.vielasegura.model.NivelAgua
import com.ruy.vielasegura.ui.theme.Borda
import com.ruy.vielasegura.ui.theme.corDestaque
import com.ruy.vielasegura.ui.theme.corForte
import com.ruy.vielasegura.ui.theme.corTexto

/** Barra superior verde-petróleo usada em todas as telas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VielaTopBar(
    titulo: String,
    subtitulo: String? = null,
    icone: ImageVector? = null,
    onVoltar: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                if (subtitulo != null) {
                    Text(subtitulo, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        navigationIcon = {
            when {
                onVoltar != null -> IconButton(onClick = onVoltar) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                icone != null -> Icon(
                    icone,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
        ),
    )
}

/** Etiqueta colorida com o nome do nível (Normal, Preparar, Atenção, Saia agora). */
@Composable
fun NivelBadge(nivel: NivelAgua, modifier: Modifier = Modifier) {
    Text(
        text = nivel.rotulo,
        color = nivel.corTexto(),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(nivel.corDestaque())
            .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

/** Barra horizontal que mostra a altura da água em relação ao limite de "Saia agora". */
@Composable
fun BarraNivel(cm: Int, modifier: Modifier = Modifier) {
    val nivel = NivelAgua.deCentimetros(cm)
    val fracao = (cm.toFloat() / NivelAgua.ALTURA_MAXIMA_CM).coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(Borda)
    ) {
        if (fracao > 0f) {
            Box(
                Modifier
                    .fillMaxWidth(fracao)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(nivel.corForte())
            )
        }
    }
}

/** Cartão branco com borda suave, padrão do app. */
@Composable
fun CartaoBase(
    modifier: Modifier = Modifier,
    borda: BorderStroke = BorderStroke(1.dp, Borda),
    onClick: (() -> Unit)? = null,
    conteudo: @Composable () -> Unit,
) {
    val forma = RoundedCornerShape(18.dp)
    val cores = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = forma, colors = cores, border = borda) {
            conteudo()
        }
    } else {
        Card(modifier = modifier.fillMaxWidth(), shape = forma, colors = cores, border = borda) {
            conteudo()
        }
    }
}

/** Mensagem exibida quando uma lista não tem dados. */
@Composable
fun EstadoVazio(
    icone: ImageVector,
    titulo: String,
    mensagem: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(
            mensagem,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** Linha "rótulo ........ valor" usada no detalhe do alerta. */
@Composable
fun LinhaInfo(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(rotulo, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(96.dp))
        Text(
            valor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}
