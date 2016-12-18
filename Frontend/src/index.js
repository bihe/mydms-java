import { render } from 'react-dom'
import { routes } from './routes';


// Declarative route configuration (could also load this config lazily
// instead, all you really need is a single root route, you don't need to
// colocate the entire config).
render(routes, document.getElementById('root'))
