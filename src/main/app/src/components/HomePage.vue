<template>
    <NavBar/>
  <div class="container-fluid text-center pt-3 ">
    <div class="row justify-content-around">
      <div class="col-md-8 pb-3">

        <div class="row justify-content-center g-2 py-2 pb-5">
            <div class="col-md-4 col-xs-12">
              <div class="input-group">
                <span class="input-group-text">Client</span>
                <small v-if="error_clients!=''" class="text-danger fw-bold">{{error_clients}}</small>
                <select v-else v-model="client" class="form-select">
                    <option value=""></option>
                    <option v-for="cl in getClients" :key="cl" :value="cl.nom">{{cl.nom}}</option>
                </select>
              </div>
            </div>
            <div class="col-md-4 col-xs-12 form-floating">
              <div class="input-group">
                <span class="input-group-text">Status</span>
                <select v-model="status" class="form-select">
                    <option value=""></option>
                    <option value="Prospecter">Prospecter</option>
                    <option value="En cours">En cours</option>
                    <option value="Reception provisoir">Reception provisoir</option>
                    <option value="Reception définitif">Reception définitif</option>
                    <option value="Cloturer">Cloturer</option>
                </select>
              </div>
            </div>
            <div class="col-xs-12 col-md-2">
                <button @click="filterProjects" class="btn btn-primary fst-italic">Chercher</button>
            </div>
        </div>
        <div v-if="error_projects!=''" class="text-danger fw-bold">{{error_projects}}</div>
        <i v-else-if="loading" class="fa-solid fa-circle-notch fa-spin mx-3"></i>
        <!-- <div v-else-if="projects.length>0" class="projects">
          <table class="table table-hover">
            <thead class="fw-bold">
              <td>Date debut</td>
              <td>Date fin</td>
              <td>Projets</td>
              <td>Client</td>
            </thead>
            <tbody>
              <tr v-for="project in projects" :key="project._id" @click="redirectToDetails(project._id)">
                <td>{{project.date_debut}}</td>
                <td>{{project.date_fin}}</td>
                <td>{{project.nom}}</td>
                <td>{{project.client}}</td>
              </tr>
            </tbody>
          </table>
        </div>-->

        <DataTable v-else-if="projects.length>0" :value="projects" :scrollable="true" scrollHeight="400px" removableSort responsiveLayout="scroll">
            <Column field="date_debut" header="Date Debut" :sortable="true"></Column>
            <Column field="date_fin" header="Date Fin" :sortable="true"></Column>
            <Column field="nom" header="Nom" :sortable="true"></Column>
            <Column field="client" header="Client"></Column>
            <Column field="_id" header="">
              <template #body="slotProps">
                <i @click="redirectToDetails(slotProps.data._id)" class="fa-solid fa-circle-info fa-lg color-1 opacity-75"></i>
              </template>
            </Column>
        </DataTable>
        <div v-else>
          <h4 class="text-muted m-3">Aucun Projet</h4>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters,mapActions } from "vuex";
import axios from "axios"
import NavBar from "./home-page/NavBar.vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

export default {
  name:"HomePage",
  components:{
    NavBar,DataTable,Column
  },
  data(){
    return{
      all_projects:[],
      projects:[],
      loading:true,
      status:"",
      client:"",
      error_clients:"",
      error_projects:""
    }
  },
  computed:mapGetters(['getClients',"getApiUrl"]),
  methods:{
    redirectToDetails(id){
      this.$router.push(`/project/${id}`)
    },
    getAllProjects(){
      axios.get(`${this.getApiUrl}api/projects`)
      .then(res=>{
        this.loading=false
        this.all_projects=res.data
        this.projects=res.data
      })
      .catch(err=>{
        console.log(err.response);
        this.loading=false
        this.error_projects="error getting projects"
      })
    },
    filterProjects(){
      try {
        if(this.status===""&&this.client===""){
          this.getAllProjects()
        }else{
          if(this.status!==""){
            this.projects=this.all_projects.filter(project=>project.status===this.status)
          }
          if(this.client!==""){
            this.projects=this.all_projects.filter(project=>project.client===this.client)
          }
        }
      } catch (error) {
        console.log(error);
      }
    },
    ...mapActions(['fetchAllClients']),
  },
  mounted(){
    this.getAllProjects()
    this.fetchAllClients()
    .catch(err=>{
      console.log(err.response);
      this.error_clients='error getting clients list'
    })
  }
}
</script>

<style scoped>
  /* .projects{
    height: 300px;
    overflow-y: auto;
  }
  tr:hover{cursor: pointer;}*/
  .opacity-75:hover{opacity: 1 !important;}
  span{font-style: italic;color:gray ;}
  .p-datatable{box-shadow: 1px 2px 5px grey;}
  /* .p-datatable .p-datatable-thead > tr > th {} */
</style>
