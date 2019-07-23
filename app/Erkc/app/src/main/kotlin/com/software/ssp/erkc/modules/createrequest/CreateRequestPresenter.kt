package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class CreateRequestPresenter @Inject constructor(view: ICreateRequestView) : RxPresenter<ICreateRequestView>(view), ICreateRequestPresenter {


    override var requestId: Int? = null

    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var requestRepository: RequestRepository

    override fun onViewLoadWithEditMode() {
        fetchRequestById(requestId!!)
    }

    override fun onViewAttached() {
        fetchDataForDropdowns()
    }

    private fun fetchDataForDropdowns() {
        subscriptions += realmRepository.fetchTypeHouseList()
                .subscribe { typeHouseList ->
                    val data = ArrayList<String>()
                    typeHouseList.forEach {
                        data.add(it.name!!)
                    }
                    view?.setTypeHouseSpinner(data)
                }
        // TODO add fetch request types
    }

    private fun fetchRequestById(id: Int) {
        subscriptions += realmRepository.fetchRequestById(id = id)
                .subscribe(
                        { realmRequest ->
                            view?.setFieldByRealmRequest(realmRequest = realmRequest)

                        },
                        { error ->
                            error.printStackTrace()
                        }
                )
    }

    override fun fetchCompanies(fias: String) {
        subscriptions += requestRepository.fetchCompaniesList(fias)
                .subscribe(
                        { it ->
                            val data = ArrayList<String>()
                            it.forEach {
                                data.add(it.name!!)
                            }
                            view?.setCompaniesSpinner(data)
                        },
                        { error ->
                            view?.onFetchCompaniesError()
                            error.printStackTrace()
                        }
                )
    }

    override fun fetchDraftData(oldUUID: String) {
        subscriptions += realmRepository.fetchDraftRequestById(oldUUID)
                .subscribe(
                        {
                            view?.setDraftData(it)
                            deleteOldValue(oldUUID)
                        },
                        { error ->
                            error.printStackTrace()
                            view?.showMessage("Ошибка при загрузке черновика")
                        }
                )
    }

    override fun deleteOldValue(id:String){
        subscriptions+=realmRepository.deleteDraftRequestById(id)
                .subscribe()
    }

    override fun saveDraftRequest(realmDraft: RealmDraft) {
        subscriptions += realmRepository.saveDraftRequest(realmDraft)
                .subscribe(
                        {
                            view?.showMessage("Заявка сохранена в черновики")

                        },
                        { error ->
                            error.printStackTrace()
                            view?.showMessage("Ошибка сохранения черновика")
                        }
                )
    }

    override fun onTryAgainClicked(fias: String) {
        fetchCompanies(fias)
    }

    override fun onAddressFieldClick() {
        view?.navigateToSearchAddress()
    }
}