# Análise Completa do Projeto DragonMineZ

> **Versão do Projeto:** 2.0.4  
> **Minecraft:** 1.20.1  
> **Forge:** 47.4.10  
> **Data da Análise:** Junho 2026

---

## Sumário

1. [Visão Geral](#1-visão-geral)
2. [Pontos Fortes](#2-pontos-fortes)
3. [Problemas de Qualidade de Código](#3-problemas-de-qualidade-de-código)
4. [Problemas de Arquitetura](#4-problemas-de-arquitetura)
5. [Problemas de Build e Tooling](#5-problemas-de-build-e-tooling)
6. [Problemas de Segurança e Performance](#6-problemas-de-segurança-e-performance)
7. [Problemas de Manutenibilidade](#7-problemas-de-manutenibilidade)
8. [Recomendações Prioritárias](#8-recomendações-prioritárias)
9. [Roadmap de Melhorias](#9-roadmap-de-melhorias)

---

## 1. Visão Geral

**DragonMineZ** é um mod de **Minecraft Forge** que traz a experiência completa do universo *Dragon Ball* para Minecraft. O projeto está em desenvolvimento ativo, com ~200+ arquivos Java fonte e uma base de código substancial.

### Escopo do Projeto

- **5 raças jogáveis:** Saiyan, Namekian, Majin, Human, Frost Demon, Bio-Android
- **3 dimensões customizadas:** Namek, Otherworld, Hiperbólica
- **Sistema de Missões:** Sagas principais + missões secundárias (data-driven via JSON)
- **Sistema de Stats:** 6 atributos base, transformações, mastery, treinamento
- **40+ entidades:** Bosses de saga, NPCs mestres, animais, dragões (Shenlong, Porunga)
- **Sistema de Combate:** Ki blasts, discos, lasers, combos, lock-on, auras
- **GUI Customizada:** Criação de personagem, stats, habilidades, scouter, HUD
- **Persistência:** MariaDB via HikariCP com fallback para JSON
- **Traduções:** 30+ idiomas via Crowdin
- **Rede:** 31 pacotes (20 C2S + 11 S2C)

---

## 2. Pontos Fortes

### 2.1 Data-Driven Design
- Raças, formas, missões e desejos são carregados de arquivos JSON — é possível adicionar conteúdo sem modificar código Java.
- Sistema de formulários é extensível via configuração.

### 2.2 Armazenamento Pluggável
- Interface `IDataStorage` bem projetada com implementações para Database (MariaDB) e JSON.
- Suporte a save assíncrono com `CompletableFuture`.

### 2.3 Config Versionada
- Sistema de config com `CURRENT_VERSION` e migração automática mantendo backups.
- Arquivos JSON com Gson, geração automática de defaults.

### 2.4 Sistema de Eventos Customizados
- Eventos DMZ bem definidos (`StatChangeEvent`, `KiChargeEvent`, `FusionEvent`, etc.) usando o EventBus do Forge.

### 2.5 Data Generation Abrangente
- Cobre recipes, loot tables, models, tags, worldgen e advancements.

### 2.6 Segurança de Dependências
- Overrides explícitos de versão para Guava, Netty e Commons-Compress por vulnerabilidades.
- Dependabot configurado para GitHub Actions e Gradle.

---

## 3. Problemas de Qualidade de Código

### 3.1 God Classes (Arquivos Muito Longos)

30+ arquivos excedem 400 linhas. Os piores:

| Arquivo | Linhas | Problema |
|---------|--------|----------|
| `HairEditorScreen.java` | ~1400 | GUI monolítica: rendering + layout + input |
| `CharacterCustomizationScreen.java` | ~1234 | Mesmo padrão — god class de GUI |
| `QuestsMenuScreen.java` | ~1045 | GUI + lógica de quests misturados |
| `CharacterStatsScreen.java` | ~1014 | GUI + lógica de stats |
| `AuraRenderHandler.java` | ~936 | Cache estático + rendering + partículas + eventos |
| `DefaultFormsFactory.java` | ~932 | ~80 definições de forma quase idênticas |
| `ConfigManager.java` | ~865 | Load/save/validation/race setup — faz tudo |
| `SideQuestsMenuScreen.java` | ~855 | Duplicata do QuestsMenuScreen |
| `SkillsMenuScreen.java` | ~725 | Mesmo padrão |
| `StatsData.java` | ~663 | Dados + cálculos + serialização NBT misturados |
| `SagaManager.java` | ~640 | Config loading + lógica de quests |
| `StoryCommand.java` | ~600 | if/else gigante |
| `TransformationsHelper.java` | ~595 | Lógica de transformações |
| `StatsEvents.java` | ~588 | 15+ subscribers de eventos |
| `CombatEvent.java` | ~578 | Lógica de combate com magic numbers |

### 3.2 Duplicação de Código

1. **Métodos de drain em StatsData.java (~300-400):**
   - `getAdjustedStaminaDrain()`, `getAdjustedEnergyDrain()`, `getAdjustedHealthDrain()` — ~60 linhas cada, idênticos exceto pelo nome do campo.

2. **DefaultFormsFactory.java (~40-850):**
   - Cada forma (x2, x3, x4, ..., mastered, buffed, fullPower, etc.) tem 15+ chamadas setter idênticas.
   - Um builder pattern reduziria ~700 linhas.

3. **Telas GUI duplicadas:**
   - `QuestsMenuScreen` (~1045), `SideQuestsMenuScreen` (~855), `SkillsMenuScreen` (~725) compartilham padrões quase idênticos de init/render/button layout.

4. **Mixin do Player.attack() original do Minecraft:**
   - `PlayerMixin.java` (~140 linhas) replica o método `attack()` vanilla — extremamente frágil para updates.

### 3.3 Error Handling Inadequado

1. **11 catch blocks vazios (Exception engolida):**
   - `Skills.java:55`, `DMZPlayerModel.java:201`, `HairEditorScreen.java:616`, `CharacterCustomizationScreen.java:414`, `MasteryCommand.java:61/80`, `StackMasteryCommand.java:56/70`, `StoryCommand.java:55`, `CosmeticArmorCompat.java:28`, `DMZThirdPartyLayerForwarder.java:116`

2. **50+ catch(Exception genérico):**
   - `ConfigManager.java` — 10+ blocos
   - `StoryCommand.java` — 11 blocos
   - `WorldGuardCompat.java` — 12 blocos

3. **printStackTrace() para stderr:**
   - `ReloadCommand.java:68`
   - `OtherworldRegionLoader.java:71`

4. **Nenhuma exceção customizada definida.**

### 3.4 Null Safety Inconsistente

1. **DMZSkinLayer.java:** 25+ null checks defensivos.
2. **DMZRacePartsLayer.java:** 30+ null checks.
3. **Uso excessivo de `.orElse(null)`** — derrota o propósito de `Optional`:
   - `DMZRendererCache.java:62/86`, `DMZWeaponsLayer.java:38`, `DMZRacePartsLayer.java:54`, `DMZSkinLayer.java:184`
4. **Encadeamento perigoso:** `player.getAttribute(Attributes.ARMOR_TOUGHNESS)` — pode ser null em certas entidades.

### 3.5 Resource Leaks

1. **HairManager.java (~139-141, ~176, ~208-210, ~241):**
   - `DataOutputStream`/`DataInputStream` nunca fechados em caso de exceção — deveria usar try-with-resources.

### 3.6 Magic Numbers e Hardcoded Strings

1. **StatsData.java:**
   - `/ 6` (nível), `* 0.25` (dano), `/ 100.0` (power release) — dezenas de magic numbers.
2. **CombatEvent.java:**
   - `0.2F + adjustedStrength * adjustedStrength * 0.8F`, `* 3.0`, `0.5` (knockback).
3. **Cores hex hardcoded:**
   - `"#F5D5A6"`, `"#000000"`, `"#FFFFFF"` repetidos em Character.java, telas GUI e HUDs.
4. **NBT keys hardcoded:**
   - `"Race"`, `"Gender"`, `"Class"`, `"HairId"`, etc. em Character.java e StatsData.java.

---

## 4. Problemas de Arquitetura

### 4.1 Acoplamento via Singletons Estáticos

Praticamente todos os sistemas são singletons estáticos:

- `ConfigManager` — chamado diretamente de `StatsData`, event handlers, comandos
- `NetworkHandler` — acesso estático global
- `StorageManager` — singleton com estratégia pluggável (única exceção positiva)
- `StatsCapability.INSTANCE` — field público estático

**Consequência:** Testabilidade quase zero. Trocar implementações requer editar código.

### 4.2 God Classes Arquiteturais

| Classe | Responsabilidade | Linhas |
|--------|-----------------|--------|
| `ConfigManager` | Load + save + validate + race setup + form management | 865 |
| `ForgeCommonEvents` | Login, logout, death, respawn, block break, attack, commands, server start/stop | 373 |
| `SagaManager` | Load JSON + criar sagas programaticamente + gerenciar estado | 640 |
| `StatsData` | 11 componentes + cálculos + serialização NBT + lógica de negócio | 663 |

### 4.3 Separação Client/Server Imperfeita

1. **StatsCapability** em `common/stats/` mas tem `CLIENT_CACHE` e lógica client-side.
2. **ClientPacketHandler** em `common/network/` — deveria estar em `client/network/`.
3. **Packets S2C** em `common/network/S2C/` mas handlers referenciam classes client-only.

### 4.4 Sistema de Queries Baseado em instanceof

`StoryModeEvents.java` (~286 linhas) usa `instanceof` para verificar tipo de objetivo em tick handler:

```java
if (objective instanceof KillObjective) { ... }
else if (objective instanceof CoordsObjective) { ... }
```

**Melhor:** Polimorfismo via método `checkProgress(Player)` em cada `QuestObjective`.

### 4.5 Tick Polling Ineficiente

Objetivos de localização são verificados a cada 20 ticks via tick handler — usar eventos de movimento do jogador seria mais eficiente.

### 4.6 Race Condition em save assíncrono

`StorageManager.loadPlayer()` carrega dados async com `CompletableFuture`, mas se o jogador desconectar antes do callback, `player.connection` pode ser null (embora haja um null check).

### 4.7 Inconsistência em Construtores de Packets

Alguns packets usam `::decode`, outros `::new` — padrão inconsistente.

---

## 5. Problemas de Build e Tooling

### 5.1 Ausência Total de Testes

**Zero testes.** Não existe diretório `src/test/`, nenhuma dependência de teste (JUnit, Mockito), nenhum `@Test`. O `gameTestServer` está configurado mas não tem o que rodar.

### 5.2 PMD Configurado Mas Não Executado

10 arquivos de regras PMD em `.github/workflows/pmd/` mas:
- Nenhum plugin PMD no `build.gradle.kts`
- Nenhum step PMD no CI

### 5.3 Versionamento Duplicado

- `gradle.properties:29`: `mod_version=2.0.4`
- `Reference.java:5`: `VERSION = "2.0.4"`
- Se um for atualizado sem o outro, divergem.

### 5.4 update.json Desatualizado

`update.json` reporta `2.0.3` como latest/recommended — código atual é `2.0.4`.

### 5.5 Access Transformer Referenciado Mas Inexistente

`build.gradle.kts:119`:
```kotlin
accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
```
O arquivo `accesstransformer.cfg` **não existe** em `src/main/resources/META-INF/`.

### 5.6 Kotlin Plugin Não Utilizado

`settings.gradle:12` declara `org.jetbrains.kotlin.jvm` mas ele nunca é aplicado no `build.gradle.kts`.

### 5.7 Offline Mode Pode Causar Falhas

`org.gradle.offline=true` em `gradle.properties` — se o cache estiver vazio, o build falha silenciosamente.

### 5.8 Nenhum Linter/Formatter

Sem Spotless, Checkstyle, EditorConfig ou pre-commit hooks.

### 5.9 src/generated/resources/ Parece Vazio

Diretório de data generation existe mas pode estar vazio ou não regenerado.

---

## 6. Problemas de Segurança e Performance

### 6.1 Thread Safety

1. **AuraRenderHandler.java (64-87):**
   - 8 `HashMap` fields (não `ConcurrentHashMap`) acessados de render thread e tick events **sem sincronização**.
   - Risco de `ConcurrentModificationException` e race conditions visuais.

2. **ConfigManager.java (30-48):**
   - `RACE_STATS`, `RACE_CHARACTER`, `RACE_FORMS`, etc. — mutáveis, sem sincronização.
   - `reload()` limpa e repopula concorrentemente com leituras.

### 6.2 DistExecutor.unsafeRunWhenOn

`DragonMineZ.java:18-19` usa `unsafeRunWhenOn` (depreciado) — risco de class loading no lado errado.

### 6.3 Broad @SuppressWarnings

- `MainItems.java:18`: `@SuppressWarnings("ALL")` — suprime **todos** os warnings, incluindo os importantes.

---

## 7. Problemas de Manutenibilidade

### 7.1 Falta de Documentação

- **JavaDoc:** Quase inexistente no código.
- **README:** Sem instruções de build, sem arquitetura overview.
- **CHANGELOG:** Inexistente.

### 7.2 Traduções Não Commitadas

Apenas `en_us.json` está no repositório. Usuários compilando da fonte veem apenas inglês.

### 7.3 Dependências Comentadas no build.gradle.kts

Linhas 196-200: Explorer's Compass, Nature's Compass, Fantasy Armor, Epic Paladins — comentadas, dead code.

---

## 8. Recomendações Prioritárias

### 🔥 Crítico (Impacto Imediato)

| # | Recomendação | Esforço | Impacto |
|---|-------------|---------|---------|
| 1 | **Fixar thread safety do AuraRenderHandler** — trocar `HashMap` por `ConcurrentHashMap` | Baixo | Alto |
| 2 | **Adicionar try-with-resources no HairManager** — fechar streams corretamente | Baixo | Alto |
| 3 | **Criar builder pattern para DefaultFormsFactory** — eliminar ~700 linhas de boilerplate | Médio | Alto |
| 4 | **Extrair métodos de drain em StatsData** — parametrizar `getAdjustedXxxDrain()` | Baixo | Médio |
| 5 | **Corrigir empty catch blocks** — pelo menos loggar a exceção | Baixo | Médio |

### 📈 Alto Impacto

| # | Recomendação | Esforço | Impacto |
|---|-------------|---------|---------|
| 6 | **Quebrar God Classes** — `ConfigManager`, `ForgeCommonEvents`, `StatsData`, `SagaManager` | Alto | Alto |
| 7 | **Criar suite de testes** — começar com testes unitários para StatsData e quest system | Alto | Alto |
| 8 | **Substituir instanceof por polimorfismo** em QuestObjective | Médio | Alto |
| 9 | **Mover ClientPacketHandler** para `client/network/` | Baixo | Médio |
| 10 | **Criar abstraction sobre ConfigManager** — quebrar acoplamento direto | Médio | Alto |

### 🛠 Manutenção

| # | Recomendação | Esforço | Impacto |
|---|-------------|---------|---------|
| 11 | **Unificar versionamento** — usar Gradle-generated build info | Baixo | Médio |
| 12 | **Atualizar update.json** para 2.0.4 | Baixo | Baixo |
| 13 | **Criar/verificar accesstransformer.cfg** ou remover referência | Baixo | Médio |
| 14 | **Remover Kotlin plugin não utilizado** | Baixo | Baixo |
| 15 | **Adicionar Spotless/Checkstyle** para formatação automática | Médio | Médio |
| 16 | **Adicionar JavaDoc** para classes públicas e métodos complexos | Alto | Médio |
| 17 | **Criar CHANGELOG.md** | Baixo | Médio |

### 🎯 Qualidade de Código

| # | Recomendação | Esforço | Impacto |
|---|-------------|---------|---------|
| 18 | **Extrair magic numbers para constantes/config** em StatsData e CombatEvent | Médio | Médio |
| 19 | **Criar constantes para NBT keys** em Character.java | Baixo | Baixo |
| 20 | **Criar constantes para cores hex** | Baixo | Baixo |
| 21 | **Extrair base class para telas GUI** — eliminar duplicação entre Quests/SideQuests/SkillsMenuScreen | Alto | Alto |
| 22 | **Extrair mixin de Player.attack()** para ser mais resiliente a updates | Médio | Alto |

---

## 9. Roadmap de Melhorias

### Fase 1 — Quick Wins (1-2 semanas)

1. Thread safety no AuraRenderHandler (`ConcurrentHashMap`)
2. try-with-resources no HairManager
3. Logging em empty catch blocks
4. Builder pattern no DefaultFormsFactory
5. Unificar versionamento
6. Atualizar update.json

### Fase 2 — Arquitetura (3-4 semanas)

7. Extrair métodos de drain em StatsData
8. Criar base class para GUI screens
9. Substituir instanceof por polimorfismo em QuestObjective
10. Quebrar ConfigManager em serviços especializados
11. Extrair constantes e magic numbers

### Fase 3 — Infraestrutura (4-6 semanas)

12. Adicionar PMD/Spotless ao build
13. Criar suite de testes (JUnit + Mockito)
14. Adicionar JavaDoc
15. Pipeline CI com lint + testes

### Fase 4 — Refinamento (contínuo)

16. Modularização de features
17. Remover acoplamento estático
18. Melhorar documentação do README
19. Criar dev guide / arch overview

---

## Resumo de Métricas

| Categoria | Nota | Principais Problemas |
|-----------|------|---------------------|
| **Qualidade de Código** | 5/10 | God classes, duplicação, exception handling |
| **Arquitetura** | 6/10 | Acoplamento estático, separação client/server |
| **Build & Tooling** | 5/10 | Zero testes, PMD não executado, versionamento duplicado |
| **Performance** | 7/10 | Thread safety no AuraRenderHandler |
| **Manutenibilidade** | 5/10 | Falta docs, JavaDoc, testes |
| **Segurança** | 7/10 | Boa gestão de dependências, threads poderiam ser melhores |
| **Testes** | 1/10 | Zero cobertura |
| **Overall** | **5.5/10** | Base sólida com débito técnico significativo |

---

## Conclusão

O **DragonMineZ** é um projeto ambicioso com uma base sólida em muitos aspectos — sistema data-driven, armazenamento pluggável, configuração versionada e bom uso do ecossistema Forge. No entanto, há **débito técnico significativo** nas áreas de:

- **Testes** (zero cobertura)
- **Qualidade de código** (god classes, duplicação)
- **Arquitetura** (acoplamento estático, falta de abstrações)

As recomendações prioritárias focam em **quick wins** de segurança/performance (thread safety, resource leaks) seguidos pela **redução de débito técnico** (god classes, testes, padronização). O projeto está em boa posição para evoluir com investimento focado nessas áreas.

---

*Documento gerado em Junho 2026 — análise baseada no estado atual do repositório `dragonminez`.*
