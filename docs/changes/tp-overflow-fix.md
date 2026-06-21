# Correção: Overflow de TP (Training Points) + Abreviação na UI

## Problema

O TP (Training Points) era armazenado como `int` (32-bit signed, max ~2.147B).
Quando o valor ultrapassava ~2.1B, o próximo ganho causava **overflow para negativo**,
e o `Math.max(0, negativo)` zerava o saldo — o TP simplesmente "sumia".

## Correção Aplicada

### 1. Mudança de `int` para `long` em todo o sistema de TP

| Arquivo | Mudança |
|---------|---------|
| `Resources.java` | `trainingPoints` `int` → `long`; getter/setter/add/remove agora usam `long`; NBT `putInt` → `putLong` |
| `Resources.java` (load) | Compatibilidade retroativa: se o NBT antigo for `int`, carrega como `getInt()`; senão, `getLong()` |
| `DMZEvent.java` | `TPGainEvent.oldValue` e `tpGain` `int` → `long` |
| `PointsCommand.java` | `IntegerArgumentType` → `LongArgumentType`; parâmetros e variáveis `int` → `long` |
| `StatsData.java` | `calculateStatIncrease` recebe `long availableTPs`; `costAccumulated` `int` → `long` |
| `IncreaseStatC2S.java` | `availableTPs` `int` → `long` |
| `UpdateSkillC2S.java` | Comparação `getTrainingPoints() >= cost` com cast para `long` |
| `StatsCommand.java` | `currentTPs`/`newTPs` `int` → `long` |
| `TPGainEvents.java` | Todas as variáveis de TP `int` → `long` (inclusive arrays e casts) |

**Efeito:** O teto vai de ~2.1B (Integer.MAX_VALUE) para ~9.2 × 10¹⁸ (Long.MAX_VALUE).

### 2. Abreviação automática na interface

Novo arquivo: `src/main/java/com/dragonminez/common/util/TPNumberFormatter.java`

Formata valores grandes com sufixos:
- `< 1B` → formato normal (ex: `523.456`)
- `≥ 1B` → `X.XXB` (ex: `1.36B`)
- `≥ 1T` → `X.XXT`
- `≥ 1Q` → `X.XXQ`

Aplicado em `CharacterStatsScreen.java` no display do saldo de TP.

### 3. Compatibilidade retroativa (saves antigos)

No `Resources.load()`:
```java
this.trainingPoints = tag.contains("TrainingPoints", Tag.TAG_INT)
    ? tag.getInt("TrainingPoints")
    : tag.getLong("TrainingPoints");
```
Isso garante que jogadores existentes não percam TP ao atualizar o mod.

## Como Replicar o Build

```bash
# 1. Gerar recursos de dados (worldgen, biomas, etc.)
./gradlew runData

# 2. Build sujo (limpo + jar)
./gradlew clean jar

# 3. O .jar estará em:
#    build/libs/dragonminez-<versão>-slim.jar
```

## Arquivos Modificados

| Arquivo | Tipo |
|---------|------|
| `src/main/java/com/dragonminez/common/stats/Resources.java` | Core fix |
| `src/main/java/com/dragonminez/common/events/DMZEvent.java` | Evento TP |
| `src/main/java/com/dragonminez/server/commands/PointsCommand.java` | Comando `/dmzpoints` |
| `src/main/java/com/dragonminez/common/stats/StatsData.java` | Cálculo de custos |
| `src/main/java/com/dragonminez/common/network/C2S/IncreaseStatC2S.java` | Packet de stat increase |
| `src/main/java/com/dragonminez/common/network/C2S/UpdateSkillC2S.java` | Packet de skills |
| `src/main/java/com/dragonminez/server/commands/StatsCommand.java` | Comando `/dmzstats reset` |
| `src/main/java/com/dragonminez/server/events/players/TPGainEvents.java` | Eventos de ganho de TP |
| `src/main/java/com/dragonminez/client/gui/character/CharacterStatsScreen.java` | Display de TP na UI |
| `src/main/java/com/dragonminez/common/util/TPNumberFormatter.java` | **(novo)** Utilitário de formatação |
