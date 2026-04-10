# Contributing to DeliverX

Это руководство описывает как мы работаем с Git в этом проекте.
Прочитай перед первым коммитом — займёт 1-3 минуты.

---

## Ветки

Мы используем **GitHub Flow**: одна главная ветка `main` + короткоживущие ветки под каждую задачу.

**`main`** — всегда рабочая. Прямой push запрещён. Попадает только через Pull Request.

### Именование веток

```
тип/краткое-описание-через-дефис
```

| Префикс | Когда использовать | Пример |
|---|---|---|
| `feat/` | Новая функциональность | `feat/rates-sorting` |
| `fix/` | Исправление бага | `fix/pom-dependencies` |
| `refactor/` | Рефакторинг без изменения поведения | `refactor/dto-to-records` |
| `chore/` | Конфиги, зависимости, CI, инфраструктура | `chore/update-gitignore` |
| `docs/` | Только документация | `docs/api-contracts` |
| `test/` | Только тесты | `test/rates-controller` |

**Правила:**
- lowercase, только латиница, слова через дефис
- Коротко и по делу: `feat/carrier-client`, а не `feat/add-new-carrier-client-class-for-http-calls`

---

## Коммиты

Формат — [Conventional Commits](https://www.conventionalcommits.org/):

```
тип(область): краткое описание в настоящем времени
```

**Область** — опциональная, указывает на часть проекта: `rates`, `auth`, `gateway`, `pom`, `dto`, `config`, и тд.

### Примеры хороших коммитов

```
feat(rates): add sorting by price and delivery time
fix(pom): replace non-existent spring-boot-starter-webmvc
refactor(dto): convert RateRequest and RateResponse to records
chore(config): set rates-service port to 8081
docs(api): add POST /rates/calculate request/response contract
test(rates): add integration tests for validation and sorting
```

### Примеры плохих коммитов

```
fixed stuff          ← что именно? где?
WIP                  ← не коммить незаконченное в общую ветку
правки               ← только латиница
fix                  ← слишком коротко
Исправил баг в pom   ← формат не соблюдён
```

**Правила:**
- Описание — в настоящем времени: `add`, `fix`, `update`, не `added`, `fixed`
- Первая буква строчная
- Без точки в конце
- Один коммит = одно логическое изменение
- Не бойся делать много маленьких коммитов — это лучше, чем один огромный

---

## Merge и Pull Request
 
**Merge** — команда Git. Берёт коммиты из одной ветки и добавляет их в другую. Работает локально, без GitHub. Просто техническая операция слияния.
 
**Pull Request** — фича GitHub, не команда Git. Это оформленное предложение сделать merge: GitHub показывает все изменения построчно, участники могут оставить комментарии, и merge происходит только после того как кто-то проверил код и нажал кнопку. В конце PR всё равно делает merge — просто осознанный, с историей обсуждений.
 
Короче: PR — это обёртка вокруг merge, которая добавляет проверку перед ним.
 
### Почему нельзя просто делать merge напрямую в main
 
"Пушить в main" — значит делать `git push`, находясь в ветке `main`. Коммиты летят напрямую в главную ветку на GitHub, минуя любую проверку.
 
```bash
# Так делать нельзя в командной работе:
git checkout main
git commit -m "feat: add something"
git push origin main        # ← коммит сразу в main, никто не видел
```
 
Проблема не техническая — Git не запрещает это. Проблема в том, что:
- никто не проверил код до того как он попал в общую ветку
- если ты сломал что-то, напарник это получит при следующем `git pull`
- нет истории обсуждений — непонятно зачем было сделано изменение
 
Поэтому `main` можно защитить через branch protection rule: GitHub физически отклонит прямой push и попросит открыть PR.
 
```bash
# Правильный путь — всегда через ветку и PR:
git checkout -b feat/something   # новая ветка
git commit -m "feat: add something"
git push origin feat/something   # пуш ветки, не main
# → открываешь PR на GitHub → напарник смотрит → merge
```

### Цикл работы над задачей

```bash
# 1. Обновить main перед началом
git checkout main
git pull origin main

# 2. Создать ветку
git checkout -b feat/your-feature-name

# 3. Работать, коммитить
git add .
git commit -m "feat(rates): add carrier http client interface"

# 4. Запушить ветку
git push origin feat/your-feature-name

# 5. Открыть Pull Request на GitHub
# 6. Дождаться ревью → merge → ветка удаляется автоматически
```

---

## Частые ситуации

### Main обновился пока я работал в своей ветке

```bash
git checkout main
git pull origin main
git checkout feat/your-feature-name
git rebase main          # переносит твои коммиты поверх свежего main
# если конфликты — решаешь их, затем:
git rebase --continue
git push origin feat/your-feature-name --force-with-lease
```

### Закоммитил не в ту ветку

```bash
# Если ещё не запушил:
git reset HEAD~1          # отменяет последний коммит, изменения остаются
git checkout -b правильная-ветка
git add .
git commit -m "..."
```

### Хочу посмотреть что изменилось перед коммитом

```bash
git diff              # изменения в отслеживаемых файлах
git diff --staged     # изменения которые уже в git add
git status            # общая картина
git log --oneline -10 # последние 10 коммитов
```

---

## Если что-то непонятно

Спроси в чате перед тем как делать — лучше уточнить и выглядеть курытм(-ой), что-то делающим(-ей).
