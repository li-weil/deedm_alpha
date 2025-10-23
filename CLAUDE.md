# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Deedm is a mathematics teaching assistant tool being migrated from a Java Swing desktop application to a modern web application using Vue 3 + Spring Boot. The project covers propositional logic, set theory, relations, functions, combinatorics, algebra, and graph theory.

## Architecture

### Frontend (Vue 3)
- **Framework**: Vue 3 with Composition API
- **UI Library**: Element Plus
- **Math Rendering**: KaTeX for formula display
- **State Management**: Pinia
- **Build Tool**: Vite
- **Location**: `/frontend/`

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0
- **Java Version**: 17
- **Database**: H2 (in-memory) with PostgreSQL support
- **API Documentation**: OpenAPI/Swagger
- **Context Path**: `/api`
- **Location**: `/backend/`

### Legacy Code Integration
- **Source**: `/legacy/` - Original Java Swing code
- **Migration Target**: `/backend/src/main/java/com/deedm/legacy/`
- **Package Prefix**: `com.deedm.legacy`
- **Purpose**: Core mathematical algorithms and business logic

## Development Commands

### Frontend Development
```bash
cd frontend
npm run dev          # Start development server
npm run build        # Build for production
npm run preview      # Preview production build
npm run lint         # Run ESLint
npm run format       # Format code with Prettier
```

### Backend Development
```bash
cd backend
mvn spring-boot:run        # Start development server
mvn test                   # Run tests
mvn clean compile          # Clean and compile
mvn package                # Package as JAR
```

### Full Application
- Frontend runs on `http://localhost:3000`
- Backend runs on `http://localhost:8080/api`
- H2 Console: `http://localhost:8080/api/h2-console`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`

## Key Development Patterns

### Legacy Code Migration Rule
**CRITICAL**: When using legacy backend code, ALWAYS migrate the source from `/home/admin-unix/Deedm/legacy` to `/home/admin-unix/Deedm/backend/src/main/java/com/deedm/legacy` first, then add the `com.deedm.legacy` package prefix.

### API Design Pattern
- All backend routes should follow `/api/...` format (nginx proxy configured),which is noted in `backend/src/main/resources/application.yml`
- Use `@RestController` with `@CrossOrigin(origins = "*")`
- Services should call legacy code from `com.deedm.legacy` package
- Return structured responses with proper error handling

### Frontend API Call Pattern
**IMPORTANT**: Always use fetch with absolute URL for mobile compatibility

```javascript
// 推荐的API调用模式
const callBackendApi = async (endpoint, options = {}) => {
  try {
    // 使用绝对路径，确保移动端也能正确访问
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api${endpoint}`, {
      method: 'POST', // 或 'GET', 'PUT', 'DELETE'
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    })

    const result = await response.json()

    if (!response.ok) {
      throw new Error(result.message || `HTTP error! status: ${response.status}`)
    }

    return result
  } catch (error) {
    console.error(`API调用失败 (${endpoint}):`, error)
    throw error
  }
}

// 使用示例
const cleanupData = async () => {
  const result = await callBackendApi('/cleanup/data', {
    method: 'POST'
  })

  if (result.success) {
    console.log(`清理了 ${result.deletedCount} 个文件`)
  }
}

const getDataStatus = async () => {
  const result = await callBackendApi('/cleanup/status')
  return result.fileCount
}
```

**关键原则:**
- 使用 `window.location.origin` 确保移动端兼容
- 优先使用 `fetch` 而非 `axios` (保持代码一致性)
- 始终包含错误处理和响应验证
- 使用结构化的响应格式 (`{success, message, data}`)

### Frontend Component Pattern
- Use Vue 3 Composition API with `<script setup>`
- Components for mathematical functions go in `/frontend/src/components/[domain]/`
- Views go in `/frontend/src/views/`
- Use Element Plus for UI components
- Mathematical formulas use KaTeX rendering via `MathRenderer.vue`

### Formula Rendering
- **Primary Method**: KaTeX for frontend formula display
- **Fallback**: LaTeX string processing for complex table generation
- **Component**: `MathRenderer.vue` handles all formula rendering
- **Format**: Use LaTeX format for all mathematical expressions

## Project Structure

```
Deedm/
├── backend/                          # Spring Boot backend
│   ├── src/main/java/com/deedm/
│   │   ├── controller/              # REST controllers
│   │   ├── service/                 # Business logic layer
│   │   ├── model/                   # Data models
│   │   ├── legacy/                  # Migrated legacy code
│   │   │   └── proplogic/           # Propositional logic
│   │   │       ├── formula/         # Formula classes
│   │   │       ├── equiv/           # Equivalence calculus
│   │   │       ├── normalFormula/   # Normal forms
│   │   │       └── reason/          # Logic reasoning
│   │   └── DeedmApplication.java    # Main application
│   └── src/main/resources/
│       └── application.yml          # Configuration
├── frontend/                         # Vue 3 frontend
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/              # Shared components
│   │   │   │   └── MathRenderer.vue # Formula rendering
│   │   │   └── logic/               # Logic components
│   │   │       └── TruthTableInterface.vue
│   │   ├── views/
│   │   │   └── MainView.vue         # Main application view
│   │   └── App.vue                  # Root component
│   └── package.json
├── legacy/                           # Original Java Swing code
│   └── src/                         # Source to migrate from
└── req/                             # Development requirements
```

## Mathematical Domains

### 1. Propositional Logic (命题逻辑)
- **Formula Builder**: Parse and construct logical formulas
- **Truth Table Generation**: Generate and analyze truth tables
- **Normal Forms**: Convert between CNF, DNF, and principal normal forms
- **Equivalence Calculus**: Check formula equivalence
- **Logic Reasoning**: Validate logical arguments

### 2. Set Theory & Relations (集合关系函数)
- Set operations (union, intersection, difference, complement)
- Relation operations and properties
- Relation closure algorithms
- Equivalence relations
- Partial order relations
- Function properties

### 3. Combinatorics (组合计数)
- String counting with constraints
- Recurrence relation solving
- Expression calculation
- Permutation and combination generation

## Configuration Notes

### Backend Configuration (`application.yml`)
- Server runs on `0.0.0.0:8080`
- Context path: `/api`
- CORS enabled for all origins
- H2 in-memory database for development
- OpenAPI documentation at `/api-docs`

### Frontend Configuration
- Vite development server on port 3000
- Auto-imports for Vue and Element Plus
- KaTeX integration for math rendering
- Responsive design with Element Plus

## Development Guidelines

### Adding New Features
1. Check `/req/` directory for specific requirements
2. Migrate required legacy code to `backend/src/main/java/com/deedm/legacy/`
3. Create Spring Boot service and controller
4. Build Vue component with Element Plus UI
5. Use `MathRenderer.vue` for formula display
6. Test API integration and frontend rendering

### Code Quality
- Use TypeScript for frontend (migrated from JavaScript)
- Follow Java 17+ conventions for backend
- Implement proper error handling in controllers
- Use reactive data patterns in Vue components
- Maintain consistent UI styling with Element Plus

### Testing
- Backend: Use Spring Boot Test framework
- Frontend: Component testing with Vue Test Utils
- API: Test endpoints with Swagger UI or Postman
- Integration: Verify frontend-backend communication

## Deployment 
- for now deployment is not necessary.i can use the dev server
- Frontend and backend deployed separately on server
- Nginx acts as reverse proxy
- Nginx routes `/api/*` to backend `localhost:8080`
- Static files served by frontend build output

## Important Notes
- **ensuring mobile compatitivity**
- **Never modify legacy code** in `/legacy/` - always migrate and adapt
- **Use LaTeX format** for mathematical expressions
- **Follow the existing component structure** when adding new features
- **Test math rendering thoroughly** using KaTeX
- **Implement proper error handling** for all API endpoints