# Web Development Patterns

## Overview

This directory contains web projects managed by Ralph.

## Build & Validation

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Run tests
npm test

# Type check (TypeScript projects)
npm run typecheck
# or
npx tsc --noEmit

# Lint
npm run lint
```

## Patterns

- Use TypeScript for type safety
- Follow React/Next.js patterns as per project
- Use CSS Modules, Tailwind, or styled-components as per project
- Use React Query or SWR for data fetching
- Use Zustand or Redux for state management (as per project)

## Testing

- Unit tests with Jest or Vitest
- Component tests with React Testing Library
- E2E tests with Playwright or Cypress

## Browser Verification

For UI stories, verify in browser:
- Use WebFetch tool or browser MCP server
- Check responsive layouts at different breakpoints
- Test keyboard navigation and accessibility

## Common Gotchas

- Remember to update environment variables in `.env.local`
- Clear `.next` cache if seeing stale builds (Next.js)
- Check `package-lock.json` is committed for reproducible builds
- Use `use client` directive for client components in Next.js App Router
