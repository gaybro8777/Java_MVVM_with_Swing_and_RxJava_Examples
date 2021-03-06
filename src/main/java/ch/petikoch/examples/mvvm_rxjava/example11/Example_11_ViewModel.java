/**
 * Copyright (c) 2015-2016 Peti Koch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.petikoch.examples.mvvm_rxjava.example11;

import ch.petikoch.examples.mvvm_rxjava.rxjava_mvvm.IViewModel;
import ch.petikoch.examples.mvvm_rxjava.utils.SysOutUtils;
import net.jcip.annotations.ThreadSafe;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import java.awt.event.ActionEvent;

@ThreadSafe
class Example_11_ViewModel implements IViewModel<Example_11_Model> {

    public final BehaviorSubject<String> vm2v_customer = BehaviorSubject.create("Customer 42" /* initial state */);
    public final BehaviorSubject<Boolean> v2vm_vm2v_edit = BehaviorSubject.create(false /* initial state */);
    public final BehaviorSubject<Boolean> vm2v_saveButtonEnabled = BehaviorSubject.create(false /* initial state */);

    public final PublishSubject<ActionEvent> v2vm_saveButtonClick = PublishSubject.create();

    public final Example_11_ViewModel_Address addressViewModel = new Example_11_ViewModel_Address();
    public final Example_11_ViewModel_Notes notesViewModel = new Example_11_ViewModel_Notes();

    public Example_11_ViewModel() {
        wireInternally();
    }

    private void wireInternally() {
        Observable.merge(addressViewModel.vm2v_dirty, notesViewModel.vm2v_dirty)
                .map(dirtyFlagChanged -> addressViewModel.vm2v_dirty.getValue() || notesViewModel.vm2v_dirty.getValue())
                .subscribe(vm2v_saveButtonEnabled);

        v2vm_saveButtonClick.subscribe(actionEvent -> {
            // Simulating save...
            addressViewModel.vm2v_address.onNext(addressViewModel.v2vm_address.getValue());
            notesViewModel.vm2v_notes.onNext(notesViewModel.v2vm_notes.getValue());
            SysOutUtils.sysout("Saved");

            v2vm_vm2v_edit.onNext(false);
        });

        v2vm_vm2v_edit.subscribe(addressViewModel.vm2v_edit);
        v2vm_vm2v_edit.subscribe(notesViewModel.vm2v_edit);
        v2vm_vm2v_edit.subscribe(editActivated -> {
            if (!editActivated && vm2v_saveButtonEnabled.getValue()) {
                vm2v_saveButtonEnabled.onNext(false);
            }
        });
    }

    @Override
    public void connectTo(final Example_11_Model model) {
        addressViewModel.connectTo(model);
        notesViewModel.connectTo(model);
    }
}
