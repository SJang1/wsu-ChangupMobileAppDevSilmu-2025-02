# 🎮 MazeGame - Android 미로 게임

## 📱 앱 소개

사용자가 손가락으로 경로를 그려 미로를 탈출하는 게임입니다. 랜덤하게 생성되는 미로에서 시작점(녹색)부터 도착점(빨간색)까지 길을 찾아보세요!

### ✨ 주요 기능

- **랜덤 미로 생성**: 매번 새로운 20×30 크기의 미로를 생성합니다
- **터치 드래그 입력**: 손가락으로 경로를 직접 그려 미로를 탐색합니다
- **정답 보기**: 막힐 때 솔루션 경로를 확인할 수 있습니다
- **경로 초기화**: 그린 경로를 지우고 처음부터 다시 시작할 수 있습니다

---

## 🛠 사용 기술 스택

### 언어 및 프레임워크
| 기술 | 버전 | 설명 |
|------|------|------|
| **Kotlin** | 2.0.21 | Android 공식 개발 언어 |
| **Jetpack Compose** | BOM 2024.09.00 | 선언적 UI 툴킷 |
| **Material 3** | - | 최신 Material Design 컴포넌트 |

### 아키텍처 및 패턴
- **MVVM 패턴**: ViewModel을 통한 UI 상태 관리
- **단방향 데이터 흐름**: Compose의 State 관리 활용
- **알고리즘 적용**: DFS(미로 생성), BFS(미로 풀이)

### 개발 환경
| 도구 | 버전 |
|------|------|
| Gradle | 8.13 |
| Android Gradle Plugin | 8.13.1 |
| Compile SDK | 36 |
| Min SDK | 32 |
| Target SDK | 36 |
| JVM Target | 11 |

---

## 📂 프로젝트 구조

```
app/src/main/java/com/composelab/mazegame/
├── MainActivity.kt          # 앱 진입점
└── ui/
    ├── GameScreen.kt         # 게임 화면 UI 컴포저블
    ├── GameViewModel.kt      # 게임 상태 관리
    ├── Maze.kt               # 미로 데이터 모델 및 알고리즘
    └── theme/
        ├── Color.kt          # 색상 정의
        ├── Theme.kt          # 테마 설정
        └── Type.kt           # 타이포그래피
```

---

## 🔧 핵심 구현 내용

### 1. 미로 생성 알고리즘 (DFS - 깊이 우선 탐색)

```kotlin
fun generate() {
    val stack = mutableListOf<Cell>()
    var current = cells[startY][startX]
    current.visited = true
    stack.add(current)

    while (stack.isNotEmpty()) {
        current = stack.removeAt(stack.size - 1)
        val neighbors = getUnvisitedNeighbors(current)

        if (neighbors.isNotEmpty()) {
            stack.add(current)
            val neighbor = neighbors.random()
            removeWall(current, neighbor)
            neighbor.visited = true
            stack.add(neighbor)
        }
    }
}
```

**구현 방식:**
- 재귀적 백트래킹(Recursive Backtracking) 알고리즘 사용
- 스택 기반으로 방문하지 않은 이웃 셀을 탐색
- 무작위로 벽을 제거하여 통로 생성

### 2. 미로 풀이 알고리즘 (BFS - 너비 우선 탐색)

```kotlin
fun solve(): List<Pair<Int, Int>> {
    val queue = ArrayDeque<Pair<Int, Int>>()
    queue.add(Pair(startX, startY))
    
    while (queue.isNotEmpty()) {
        val (cx, cy) = queue.removeFirst()
        if (cx == endX && cy == endY) {
            // 경로 역추적하여 반환
        }
        // 상하좌우 탐색
    }
}
```

**구현 방식:**
- BFS를 통해 최단 경로 탐색
- 부모 노드 추적으로 경로 재구성

### 3. Canvas를 활용한 미로 렌더링

```kotlin
Canvas(modifier = modifier.pointerInput(Unit) {
    detectDragGestures(
        onDragStart = { offset -> onDragStart(offset) },
        onDrag = { change, _ -> onDrag(change.position) }
    )
}) {
    // 셀의 벽 그리기
    // 플레이어 경로 그리기
    // 시작점/도착점 마커 그리기
}
```

**구현 방식:**
- Jetpack Compose의 Canvas API 활용
- `pointerInput`으로 드래그 제스처 감지
- 실시간으로 사용자 경로 렌더링

### 4. ViewModel을 통한 상태 관리

```kotlin
class GameViewModel : ViewModel() {
    var maze by mutableStateOf(createAndGenerateMaze())
    var playerPath by mutableStateOf(Path())
    var solutionPath by mutableStateOf<List<Pair<Int, Int>>>(emptyList())
    
    fun generateNewMaze() { ... }
    fun resetPlayerPath() { ... }
    fun showSolution() { ... }
}
```

**구현 방식:**
- Compose의 `mutableStateOf`로 반응형 상태 관리
- ViewModel Factory 패턴 적용

---

## 🎨 UI/UX 특징

| 요소 | 설명 |
|------|------|
| 🟢 **시작점** | 녹색 원으로 표시 |
| 🔴 **도착점** | 빨간색 원으로 표시 |
| 🔵 **플레이어 경로** | 파란색 선으로 드래그 경로 표시 |
| 🟣 **정답 경로** | 마젠타색 선으로 솔루션 표시 |
| ⬛ **벽** | 검은색 선으로 미로 벽 표시 |

---

## 🚀 실행 방법

### 요구사항
- Android Studio Ladybug 이상
- JDK 21
- Android 12L (API 32) 이상 디바이스 또는 에뮬레이터

### 빌드 및 실행
Android Studio에서 프로젝트를 열고 Run 버튼을 클릭하세요.

---

## 📚 학습 포인트

이 프로젝트를 통해 학습할 수 있는 내용:

1. **Jetpack Compose 기초**
   - Composable 함수 작성
   - State 관리 및 리컴포지션
   - Canvas를 활용한 커스텀 그래픽

2. **Android 아키텍처**
   - MVVM 패턴 적용
   - ViewModel과 ViewModelFactory

3. **알고리즘**
   - 미로 생성 (DFS)
   - 경로 탐색 (BFS)

4. **사용자 입력 처리**
   - 터치/드래그 제스처 감지
   - 실시간 그래픽 업데이트

---

## 📄 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.

---

*Made with ❤️ and Kotlin by SJang1*
