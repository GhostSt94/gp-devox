import axios from "axios";
export default {
    state : {
        clients:[]
    },

    getters : {
        getClients:(state)=>state.clients
    },
    
    mutations : {
        setClients:(state,data)=>{state.clients=data}
    },
    
    actions : {
        async fetchAllClients({commit,getters}){
            const res=await axios.get(`${getters.getApiUrl}api/clients`);
            commit('setClients',res.data)
        }
    }
}