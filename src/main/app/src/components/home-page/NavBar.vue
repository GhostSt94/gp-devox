<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light w-100 shadow-sm">
        <div class="container-fluid">
            <router-link class="navbar-brand" to="/">
                <img src="@/assets/logo_devox.png">
            </router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
                <div class="navbar-nav">
                    <router-link class="nav-link rounded" to="/ajouter/projet">
                        Nouveau Projet<i class="fa-solid fa-diagram-project ms-1"></i>
                    </router-link>
                    <router-link class="nav-link rounded" to="/clients">
                        Liste des Clients<i class="fa-solid fa-building ms-1"></i>
                    </router-link>
                    <!-- <router-link class="nav-link rounded" to="/users">
                        Gestion des utilisateurs<i class="fa-solid fa-user-group ms-1"></i>
                    </router-link> -->
                    <router-link @click="out" class="nav-link rounded" to="/">
                        Logout<i class="fa-solid fa-arrow-right-from-bracket ms-1"></i>
                    </router-link>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
import {mapActions} from "vuex";
export default {
    name:"NavBar",
    methods:{
        ...mapActions(['logout']),
        out(){
          this.logout()
          .then(location.reload())
          .catch(err=>alert(err))
        }
    }
}
</script>

<style scoped>
    .navbar-brand img{
        width: 120px;
        height: 70px;
    }
    .nav-link:hover{
        background:rgb(239, 107, 41);
        color:white !important;
        font-size: bolder !important;
    }
    nav-link{
        padding-right: 5px;
    }
</style>
