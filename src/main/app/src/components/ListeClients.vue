<template>
    <div class="container pt-4">
        <i @click="goHome" class="fa-solid fa-arrow-left-long float-start text-secondary back m-2"></i>
        <h4 class="ms-5 color-1">Liste des Clients <i class="fa-solid fa-plus btn btn-primary rounded-pill ms-1" @click="goToAddPage"></i></h4>
        <div class="container">
            <div class="input-group filter m-2 my-4 m-auto search">
                <span class="input-group-text">Nom</span>
                <input @input="filterClients" type="text" class="form-control" v-model="client_filter" placeholder="Tapez un nom">
            </div>
        </div>
        <div  v-if="clients.length>0" class="row justify-content-center pt-3">
            <div @click="goToDetails(cl._id)" v-for="cl in clients" :key="cl._id" class="col-3 m-2 p-2 bg-white rounded shadow-sm client">
                <Avatar :label="cl.nom[0]" /><span class="ms-2">{{cl.nom}}</span>
            </div>
        </div>
        <div  v-else class="text-center">
            <h4 class="text-muted m-3">Aucun Projet</h4>
        </div>
        <div v-show="error!==''">
            <h6 class="text-danger text-center">{{error}}</h6>
        </div>
    </div>
</template>

<script>
import { mapGetters,mapActions } from "vuex";
import Avatar from 'primevue/avatar';
export default {
    name:"ListeClients",
    components:{
        Avatar
    },
    data(){
        return{
            clients:[],
            client_filter:null,
            error:""
        }
    },
    computed:mapGetters(['getClients']),
    methods:{
        goHome(){
            this.$router.push("/")
        },
        goToAddPage(){
            this.$router.push("/ajouter/client")
        },
        goToDetails(id){
            this.$router.push(`/clients/${id}`)
        },
        filterClients(){
            try {
                this.clients=this.getClients.filter(cl=>cl.nom.toLowerCase().startsWith(this.client_filter.toLowerCase()))
            } catch (error) {
                this.error="Error Filtering"
            }
        },
        ...mapActions(['fetchAllClients'])
    },
    mounted(){
        this.fetchAllClients().then(()=>this.clients=this.getClients)
        .catch(err=>{
            this.error="Cannot find Client list"
            console.log(err)
        })
    }
}
</script>

<style scoped>
    .client{
        color:grey;
    }
    .client:hover{
        cursor: pointer;color: rgb(239, 107, 41);font-weight: bold;
    }
    .search input:focus{
        box-shadow: 1px 1px 5px rgb(239, 107, 41);
    }
    .filter{width: 40%;}
    @media screen and (max-width: 700px){
        .filter{width: 80%;}
    }
</style>
