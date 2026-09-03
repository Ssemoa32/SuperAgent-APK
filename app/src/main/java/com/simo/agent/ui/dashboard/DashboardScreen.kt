package com.simo.agent.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// الألوان
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBg = Color(0xFF161616)
private val NeonCyan = Color(0xFF00F0FF)
private val NeonPurple = Color(0xFF8A2BE2)
private val TextPrimary = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFFAAAAAA)
private val SuccessGreen = Color(0xFF00E676)

@Composable
fun DashboardScreen(
onEnableAccessibility: () -> Unit = {},
onOpenSettings: () -> Unit = {}
) {
var agentStatus by remember { mutableStateOf("Online / Active") }
var isSafeMode by remember { mutableStateOf(false) }

Scaffold(
containerColor = DarkBackground,
bottomBar = {
BottomNavigationBar()
}
) { padding ->
LazyColumn(
modifier = Modifier
.fillMaxSize()
.padding(padding)
.padding(horizontal = 16.dp),
verticalArrangement = Arrangement.spacedBy(16.dp)
) {
item { Spacer(modifier = Modifier.height(8.dp)) }

// Header
item {
HeaderSection()
}

// Agent Status Card
item {
AgentStatusCard(status = agentStatus)
}

// Skills + Connections
item {
Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
SkillsCard(modifier = Modifier.weight(1f))
ConnectionsCard(modifier = Modifier.weight(1f))
}
}

// System Controls
item {
SystemControlsCard(
isSafeMode = isSafeMode,
onSafeModeChange = { isSafeMode = it },
onEnableAccessibility = onEnableAccessibility
)
}

// Activity Log
item {
ActivityLogCard()
}

item { Spacer(modifier = Modifier.height(20.dp)) }
}
}
}

@Composable
fun HeaderSection() {
Row(
modifier = Modifier.fillMaxWidth(),
verticalAlignment = Alignment.CenterVertically,
horizontalArrangement = Arrangement.SpaceBetween
) {
Row(verticalAlignment = Alignment.CenterVertically) {
Box(
modifier = Modifier
.size(42.dp)
.clip(RoundedCornerShape(12.dp))
.background(
Brush.linearGradient(listOf(NeonCyan, NeonPurple))
),
contentAlignment = Alignment.Center
) {
Text("S", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
}
Spacer(modifier = Modifier.width(12.dp))
Column {
Text(
text = "SimoAgent",
color = TextPrimary,
fontSize = 20.sp,
fontWeight = FontWeight.Bold
)
Text(
text = "AI Agent Framework",
color = TextSecondary,
fontSize = 12.sp
)
}
}

IconButton(onClick = { /* Settings */ }) {
Icon(Icons.Default.Settings, contentDescription = null, tint = NeonCyan)
}
}
}

@Composable
fun AgentStatusCard(status: String) {
Card(
modifier = Modifier.fillMaxWidth(),
colors = CardDefaults.cardColors(containerColor = CardBg),
shape = RoundedCornerShape(16.dp)
) {
Column(modifier = Modifier.padding(16.dp)) {
Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically
) {
Text("حالة الوكيل • Agent Status", color = TextSecondary, fontSize = 13.sp)
Row(verticalAlignment = Alignment.CenterVertically) {
Box(
modifier = Modifier
.size(8.dp)
.clip(CircleShape)
.background(SuccessGreen)
)
Spacer(modifier = Modifier.width(6.dp))
Text(status, color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
}
}

Spacer(modifier = Modifier.height(12.dp))

Text(
text = "SimoAgent يعمل بسلاسة",
color = TextPrimary,
fontSize = 16.sp,
fontWeight = FontWeight.SemiBold
)

Spacer(modifier = Modifier.height(16.dp))

Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceBetween
) {
StatusItem("Uptime", "2h 47m")
StatusItem("Tasks", "128")
StatusItem("Success", "98.6%")
}
}
}
}

@Composable
fun StatusItem(label: String, value: String) {
Column(horizontalAlignment = Alignment.CenterHorizontally) {
Text(value, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
Text(label, color = TextSecondary, fontSize = 12.sp)
}
}

@Composable
fun SkillsCard(modifier: Modifier = Modifier) {
Card(
modifier = modifier,
colors = CardDefaults.cardColors(containerColor = CardBg),
shape = RoundedCornerShape(16.dp)
) {
Column(modifier = Modifier.padding(14.dp)) {
Text("المهارات • Skills", color = TextSecondary, fontSize = 13.sp)
Spacer(modifier = Modifier.height(12.dp))

val skills = listOf(
"Code Assistant" to true,
"Web Search" to true,
"Data Analyst" to true,
"API Connector" to false
)

skills.forEach { (name, active) ->
Row(
modifier = Modifier
.fillMaxWidth()
.padding(vertical = 4.dp),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically
) {
Text(name, color = TextPrimary, fontSize = 13.sp)
Box(
modifier = Modifier
.size(8.dp)
.clip(CircleShape)
.background(if (active) SuccessGreen else Color.Gray)
)
}
}
}
}
}

@Composable
fun ConnectionsCard(modifier: Modifier = Modifier) {
Card(
modifier = modifier,
colors = CardDefaults.cardColors(containerColor = CardBg),
shape = RoundedCornerShape(16.dp)
) {
Column(modifier = Modifier.padding(14.dp)) {
Text("الاتصالات • Connections", color = TextSecondary, fontSize = 13.sp)
Spacer(modifier = Modifier.height(12.dp))

ConnectionItem("Router", true)
ConnectionItem("MCP Server", true)
ConnectionItem("OMNI Keys", false)
}
}
}

@Composable
fun ConnectionItem(name: String, connected: Boolean) {
Row(
modifier = Modifier
.fillMaxWidth()
.padding(vertical = 4.dp),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically
) {
Text(name, color = TextPrimary, fontSize = 13.sp)
Text(
text = if (connected) "Connected" else "Offline",
color = if (connected) SuccessGreen else Color.Gray,
fontSize = 12.sp
)
}
}

@Composable
fun SystemControlsCard(
isSafeMode: Boolean,
onSafeModeChange: (Boolean) -> Unit,
onEnableAccessibility: () -> Unit
) {
Card(
modifier = Modifier.fillMaxWidth(),
colors = CardDefaults.cardColors(containerColor = CardBg),
shape = RoundedCornerShape(16.dp)
) {
Column(modifier = Modifier.padding(16.dp)) {
Text("التحكم في النظام • System Controls", color = TextSecondary, fontSize = 13.sp)
Spacer(modifier = Modifier.height(16.dp))

Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceEvenly
) {
ControlButton("إيقاف", Icons.Default.Pause) {}
ControlButton("إعادة", Icons.Default.Refresh) {}
ControlButton("إعدادات", Icons.Default.Settings) {}
ControlButton("مسح", Icons.Default.Delete) {}
}

Spacer(modifier = Modifier.height(16.dp))

// زر تفعيل Accessibility
Button(
onClick = onEnableAccessibility,
modifier = Modifier.fillMaxWidth(),
colors = ButtonDefaults.buttonColors(
containerColor = NeonPurple.copy(alpha = 0.2f)
),
border = BorderStroke(1.dp, NeonPurple)
) {
Text("تفعيل Accessibility Service", color = NeonPurple)
}

Spacer(modifier = Modifier.height(12.dp))

Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically
) {
Text("Safe Mode", color = TextPrimary)
Switch(
checked = isSafeMode,
onCheckedChange = onSafeModeChange,
colors = SwitchDefaults.colors(
checkedThumbColor = NeonCyan,
checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
)
)
}
}
}
}

@Composable
fun ControlButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
Column(horizontalAlignment = Alignment.CenterHorizontally) {
IconButton(
onClick = onClick,
modifier = Modifier
.size(48.dp)
.border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
) {
Icon(icon, contentDescription = null, tint = NeonCyan)
}
Text(text, color = TextSecondary, fontSize = 11.sp)
}
}

@Composable
fun ActivityLogCard() {
Card(
modifier = Modifier.fillMaxWidth(),
colors = CardDefaults.cardColors(containerColor = CardBg),
shape = RoundedCornerShape(16.dp)
) {
Column(modifier = Modifier.padding(16.dp)) {
Text("سجل النشاط • Activity Log", color = TextSecondary, fontSize = 13.sp)
Spacer(modifier = Modifier.height(12.dp))

val logs = listOf(
"11:42:15 Agent started successfully",
"11:41:58 Router reconnected",
"11:41:47 Skill executed: Code Assistant",
"11:41:30 MCP Server synced",
"11:41:12 Task completed: Data analysis"
)

logs.forEach { log ->
Text(
text = log,
color = TextPrimary,
fontSize = 13.sp,
modifier = Modifier.padding(vertical = 3.dp)
)
}
}
}
}

@Composable
fun BottomNavigationBar() {
NavigationBar(
containerColor = CardBg,
contentColor = NeonCyan
) {
NavigationBarItem(
selected = true,
onClick = {},
icon = { Icon(Icons.Default.Home, contentDescription = null) },
label = { Text("لوحة التحكم") }
)
NavigationBarItem(
selected = false,
onClick = {},
icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
label = { Text("الوكلاء") }
)
NavigationBarItem(
selected = false,
onClick = {},
icon = { Icon(Icons.Default.Build, contentDescription = null) },
label = { Text("الأدوات") }
)
NavigationBarItem(
selected = false,
onClick = {},
icon = { Icon(Icons.Default.Settings, contentDescription = null) },
label = { Text("الإعدادات") }
)
}
}
