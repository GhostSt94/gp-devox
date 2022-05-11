import * as VueRouter from 'vue-router'
import HomePage from './components/HomePage.vue'
import AddProject from './components/add/AddProject.vue'
import AddClient from './components/add/AddClient.vue'
import ListeClients from './components/ListeClients.vue'
import ProjectDetails from './components/ProjectDetails.vue'
import AuthUser from './components/auth/AuthUser.vue'

const router = VueRouter.createRouter({
    history: VueRouter.createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'Home',
            component: HomePage
        },
        {
            path: '/clients',
            name: 'Clients',
            component: ListeClients
        },
        {
            path: '/clients/:id',
            name: 'ClientDetails',
            component: AddClient
        },
        {
            path: '/ajouter/projet',
            name: 'addProject',
            component: AddProject
        },
        {
            path: '/ajouter/client',
            name: 'AddClient',
            component: AddClient
        },
        {
            path: '/project/:id',
            name: 'details',
            component: ProjectDetails
        },
        {
            path: '/auth',
            name: 'Auth',
            component: AuthUser
        }
    ]
})

export default router