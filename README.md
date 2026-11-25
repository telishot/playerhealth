# Player Health Tags (Fabric)

Клиентский мод для Minecraft **26.1.x** (Fabric), который показывает здоровье (HP)
живых существ (игроки и мобы) над их головами — сердца и точное значение, включая поглощение
от золотых яблок.

## Требования

- Minecraft **26.1 / 26.1.2**
- Fabric Loader **0.20.1+**
- Java **25**
- Fabric API **не требуется**

## Оптимизации

- **Нет аллокаций в кадре рендера**: HP извлекается в render state один раз за
  кадр на этапе extraction; текстовая метка пересобирается только при изменении
  здоровья игрока.
- **Данные живут в render state игры**: используется duck-интерфейс на
  `LivingEntityRenderState`, который игра пулит и переиспользует. Никаких
  собственных `Map` с ключами-ссылками — утечки памяти исключены по построению.
- **Родная ванильная логика**: HP рисуется через тот же механизм, что и ник
  (`submitNameTag`), поэтому дистанция видимости, скрадывание, невидимость и
  подсветка обрабатываются Minecraft'ом бесплатно.
- Мод полностью клиентский, сервер можно не трогать.

## Управление

Мод работает сразу после установки, без конфигов.

## Установка

1. Установите [Fabric Loader](https://fabricmc.net/use/installer/) для Minecraft 26.1.
2. Положите собранный `playerhealth-1.0.0.jar` в папку `mods`.
3. Запустите игру — над другими игроками и мобами появится HP под ником.

## Сборка из исходников

Требуется JDK 25 и Gradle **9.4+**.

```bash
git clone https://github.com/telishot/playerhealth
cd playerhealth
git checkout main
git pull
git branch --show-current
gradle build
```

Готовый JAR будет в `build/libs/playerhealth-1.0.0.jar`.
Если у вас нет Gradle — откройте папку как проект в IntelliJ IDEA (2025.2+)
и соберите через панели Gradle.

## Как это работает

- `LivingEntityHealthMixin` — на этапе `extractRenderState` записывает HP всех живых существ; для безымянных мобов HP сам становится табличкой (дистанция, команды и невидимость — ванильные)
  в его render state.
- `LivingEntityRenderStateMixin` — duck-интерфейс: хранит HP, максимум и
  кэшированный `Component` с текстом.
- `EntityRendererNameTagMixin` — перехватывает ванильный вызов
  `SubmitNodeCollector#submitNameTag` внутри `EntityRenderer#submitNameDisplay`
  и отправляет вторую строку (HP) со сдвигом вниз под ником.

## Лицензия

MIT
